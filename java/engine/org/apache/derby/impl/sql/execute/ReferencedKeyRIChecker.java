/*

   Derby - Class org.apache.derby.impl.sql.execute.ReferencedKeyRIChecker

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to you under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */

package org.apache.derby.impl.sql.execute;

import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.reference.SQLState;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.StatementType;
import org.apache.derby.iapi.sql.StatementUtil;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.store.access.ScanController;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.shared.common.sanity.SanityManager;

/**
 * A Referential Integrity checker for a change
 * to a referenced key (primary or unique).   Makes
 * sure that all the referenced key row is not
 * referenced by any of its foreign keys.  see 
 * ForeignKeyRIChecker for the code that validates
 * changes to foreign keys.
 */
public class ReferencedKeyRIChecker extends GenericRIChecker
{
    private ScanController refKeyIndexScan = null;
    private DataValueDescriptor[] refKey = new DataValueDescriptor[numColumns];

	/**
     * @param lcc       the language connection context
	 * @param tc		the xact controller
	 * @param fkinfo	the foreign key information 
	 *
	 * @exception StandardException		Thrown on failure
	 */
    ReferencedKeyRIChecker(LanguageConnectionContext lcc,
                           TransactionController tc,
                           FKInfo fkinfo) throws StandardException
	{
        super(lcc, tc, fkinfo);

		if (SanityManager.DEBUG)
		{
			if (fkInfo.type != FKInfo.REFERENCED_KEY)
			{
				SanityManager.THROWASSERT("invalid type "+fkInfo.type+
					" for a ReferencedKeyRIChecker");
			}
		} 
	}

	/**
	 * Check that the row either has a null column(s), or
	 * has no corresponding foreign keys.
	 * <p> 
	 * If a foreign key is found, an exception is thrown.
	 * If not, the scan is closed.
	 *
     * @param a     the activation
	 * @param row	the row to check
     * @param restrictCheckOnly
	 *
	 * @exception StandardException on unexpected error, or
	 *		on a primary/unique key violation
	 */
    @Override
    void doCheck(Activation a,
                 ExecRow row,
                 boolean restrictCheckOnly) throws StandardException
	{
		/*
		** If any of the columns are null, then the
		** check always succeeds.
		*/
		if (isAnyFieldNull(row))
		{
			return;
		}

        if (fkInfo.refConstraintIsDeferrable) {
            // We may have more than one row if the referenced constraint is
            // deferred, if so, all is good: no foreign key constraints can be
            // violated. DERBY-6559
            if (lcc.isEffectivelyDeferred(
                    lcc.getCurrentSQLSessionContext(a),
                    fkInfo.refConglomNumber)) {
                // It *is* deferred, go see if we have more than one row
                if (isDuplicated(row)) {
                    return;
                }
            }
        }

		/*
		** Otherwise, should be no rows found.
	 	** Check each conglomerate.
		*/
		ScanController scan;

		for (int i = 0; i < fkInfo.fkConglomNumbers.length; i++)
		{
			
			if(restrictCheckOnly)
			{
				if(fkInfo.raRules[i] != StatementType.RA_RESTRICT)
					continue;
			}

			scan = getScanController(fkInfo.fkConglomNumbers[i], fkScocis[i], fkDcocis[i], row);
			if (scan.next())
			{
				close();

                final UUID fkId = fkInfo.fkIds[i];

                // Only considering deferring if we don't have RESTRICT, i.e.
                // NO ACTION. CASCADE and SET NULL handled elsewhere.
                if (fkInfo.deferrable[i] &&
                    fkInfo.raRules[i] != StatementType.RA_RESTRICT &&
                        lcc.isEffectivelyDeferred(
                                lcc.getCurrentSQLSessionContext(a), fkId)) {
                    deferredRowsHashTable =
                            DeferredConstraintsMemory.rememberFKViolation(
                                    lcc,
                                    deferredRowsHashTable,
                                    fkInfo.fkConglomNumbers[i],
                                    fkInfo.refConglomNumber,
                                    fkInfo.fkIds[i],
                                    indexQualifierRow.getRowArray(),
                                    fkInfo.schemaName,
                                    fkInfo.tableName);
                } else {

                    StandardException se = StandardException.newException(
                            SQLState.LANG_FK_VIOLATION,
                            fkInfo.fkConstraintNames[i],
                            fkInfo.tableName,
                            StatementUtil.typeName(fkInfo.stmtType),
                            RowUtil.toString(row, fkInfo.colArray));

                    throw se;
                }
			}
			/*
			** Move off of the current row to release any locks.
			*/
			scan.next();
		}
	}

    private boolean isDuplicated(ExecRow row)
            throws StandardException {
        final DataValueDescriptor[] indexRowArray = row.getRowArray();

        for (int i = 0; i < numColumns; i++)
        {
            refKey[i] = indexRowArray[fkInfo.colArray[i] - 1];
        }

        if (refKeyIndexScan == null) {
            refKeyIndexScan = tc.openScan(
                    fkInfo.refConglomNumber,
                    false,                  // no hold over commit
                    0,                      // read only
                    TransactionController.MODE_RECORD,
                                            // record locking
                    TransactionController.ISOLATION_READ_COMMITTED_NOHOLDLOCK,
                    (FormatableBitSet)null, // retrieve all fields
                    refKey,                 // startKeyValue
                    ScanController.GE,      // startSearchOp
                    null,                   // qualified
                    refKey,                 // stopKeyValue
                    ScanController.GT);     // stopSearchOp
        } else {
            refKeyIndexScan.reopenScan(
                      refKey,             // startKeyValue
                      ScanController.GE,  // startSearchOp
                      null,               // qualifier
                      refKey,             // stopKeyValue
                      ScanController.GT); // stopSearchOp
        }

        if (refKeyIndexScan.next()) {
            if (refKeyIndexScan.next()) {
                // two matching rows found, all ok
                return true;
            } // else exactly one row contains key
        } else {
            // No rows contain key
        }

        return false;
    }

    /**
     * Clean up all scan controllers
     *
     * @exception StandardException on error
     */
    void close()
        throws StandardException {

        if (refKeyIndexScan != null) {
            refKeyIndexScan.close();
            refKeyIndexScan = null;
        }

        super.close();
    }

}





