package org.apache.derbyTesting.functionTests.tests.lang;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.Test;

import org.apache.derbyTesting.junit.BaseJDBCTestCase;
import org.apache.derbyTesting.junit.JDBC;
import org.apache.derbyTesting.junit.TestConfiguration;

public class CastingTest extends BaseJDBCTestCase {

    public CastingTest(String name) {
        super(name);

    }
    public static String VALID_DATE_STRING = "'2000-01-01'";
    public static String VALID_TIME_STRING = "'15:30:20'";
    public static String VALID_TIMESTAMP_STRING = "'2000-01-01 15:30:20'";
    public static String NULL_VALUE="NULL";

    public static String ILLEGAL_CAST_EXCEPTION_SQLSTATE = "42846";
    public static String LANG_NOT_STORABLE_SQLSTATE  = "42821";
    public static String LANG_NOT_COMPARABLE_SQLSTATE = "42818";
    public static String METHOD_NOT_FOUND_SQLSTATE = "42884";
    public static String LANG_FORMAT_EXCEPTION_SQLSTATE = "22018";

    public static int SQLTYPE_ARRAY_SIZE = 17 ;
    public static int SMALLINT_OFFSET = 0;
    public static int INTEGER_OFFSET = 1;
    public static int BIGINT_OFFSET = 2;
    public static int DECIMAL_OFFSET = 3;
    public static int REAL_OFFSET = 4;
    public static int DOUBLE_OFFSET = 5;
    public static int CHAR_OFFSET = 6;
    public static int VARCHAR_OFFSET = 7;
    public static int LONGVARCHAR_OFFSET = 8;
    public static int CHAR_FOR_BIT_OFFSET = 9;
    public static int VARCHAR_FOR_BIT_OFFSET = 10;
    public static int LONGVARCHAR_FOR_BIT_OFFSET = 11;
    public static int CLOB_OFFSET = 12;
    public static int DATE_OFFSET = 13;
    public static int TIME_OFFSET = 14;
    public static int TIMESTAMP_OFFSET = 15;
    public static int BLOB_OFFSET = 16;


    public static String[] SQLTypes =
    {
            "SMALLINT",
            "INTEGER",
            "BIGINT",
            "DECIMAL(10,5)",
            "REAL",
            "DOUBLE",
            "CHAR(60)",
            "VARCHAR(60)",
            "LONG VARCHAR",
            "CHAR(60) FOR BIT DATA",
            "VARCHAR(60) FOR BIT DATA",
            "LONG VARCHAR FOR BIT DATA",
            "CLOB(1k)",
            "DATE",
            "TIME",
            "TIMESTAMP",
            "BLOB(1k)",
    };


    public static int NULL_DATA_OFFSET = 0;  // offset of NULL value
    public static int VALID_DATA_OFFSET = 1;  // offset of NULL value

    // rows are data types.
    // data is NULL_VALUE, VALID_VALUE
    // Should add Minimum, Maximum and out of range.
public static String[][]SQLData =
    {
            {NULL_VALUE, "0"},       // SMALLINT
            {NULL_VALUE,"11"},       // INTEGER
            {NULL_VALUE,"22"},       // BIGINT
            {NULL_VALUE,"3.3"},      // DECIMAL(10,5)
            {NULL_VALUE,"4.4"},      // REAL,
            {NULL_VALUE,"5.5"},      // DOUBLE
            {NULL_VALUE,"'7'"},      // CHAR(60)
            {NULL_VALUE,"'8'"},      //VARCHAR(60)",
            {NULL_VALUE,"'9'"},      // LONG VARCHAR
            {NULL_VALUE,"X'10aa'"},  // CHAR(60)  FOR BIT DATA
            {NULL_VALUE,"X'10bb'"},  // VARCHAR(60) FOR BIT DATA
            {NULL_VALUE,"X'10cc'"},  //LONG VARCHAR FOR BIT DATA
            {NULL_VALUE,"'13'"},     //CLOB(1k)
            {NULL_VALUE,VALID_DATE_STRING},        // DATE
            {NULL_VALUE,VALID_TIME_STRING},        // TIME
            {NULL_VALUE,VALID_TIMESTAMP_STRING},   // TIMESTAMP
            {NULL_VALUE,"X'01dd'"}                 // BLOB
    };




    public static final boolean _ = false;
    public static final boolean X = true;

    /**
       Table 146 - Supported explicit casts between Built-in DataTypes

       This table has THE FOR BIT DATA TYPES broken out into separate columns
       for clarity and testing
    **/


    public static final boolean[][]  T_146 = {
            
//Types.                 S  I  B  D  R  D  C  V  L  C  V  L  C  D  T  T  B
//                    M  N  I  E  E  O  H  A  O  H  A  O  L  A  I  I  L
//                    A  T  G  C  A  U  A  R  N  A  R  N  O  T  M  M  O
//                    L  E  I  I  L  B  R  C  G  R  C  G  B  E  E  E  B
//                    L  G  N  M     L     H  V  .  H  V           S
//                    I  E  T  A     E     A  A  B  .  A           T
//                    N  R     L           R  R  I  B  R           A
//                    T                       C  T  I  .           M
//                                            H     T  B           P
//                                            A        I
//                                            R        T
/* 0 SMALLINT */        { X, X, X, X, X, X, X, _, _, _, _, _, _, _, _, _, _ },
/* 1 INTEGER  */        { X, X, X, X, X, X, X, _, _, _, _, _, _, _, _, _, _ },
/* 2 BIGINT   */        { X, X, X, X, X, X, X, _, _, _, _, _, _, _, _, _, _ },
/* 3 DECIMAL  */        { X, X, X, X, X, X, X, _, _, _, _, _, _, _, _, _, _ },
/* 4 REAL     */        { X, X, X, X, X, X, _, _, _, _, _, _, _, _, _, _, _ },
/* 5 DOUBLE   */        { X, X, X, X, X, X, _, _, _, _, _, _, _, _, _, _, _ },
/* 6 CHAR     */        { X, X, X, X, _, _, X, X, X, _, _, _, X, X, X, X, _ },
/* 7 VARCHAR  */        { X, X, X, X, _, _, X, X, X, _, _, _, X, X, X, X, _ },
/* 8 LONGVARCHAR */     { _, _, _, _, _, _, X, X, X, _, _, _, X, _, _, _, _ },
/* 9 CHAR FOR BIT */    { _, _, _, _, _, _, _, _, _, X, X, X, _, _, _, _, X },
/* 10 VARCH. BIT   */   { _, _, _, _, _, _, _, _, _, X, X, X, _, _, _, _, X },
/* 11 LONGVAR. BIT */   { _, _, _, _, _, _, _, _, _, X, X, X, _, _, _, _, X },
/* 12 CLOB         */   { _, _, _, _, _, _, X, X, X, _, _, _, X, _, _, _, _ },
/* 13 DATE         */   { _, _, _, _, _, _, X, X, _, _, _, _, _, X, _, _, _ },
/* 14 TIME         */   { _, _, _, _, _, _, X, X, _, _, _, _, _, _, X, _, _ },
/* 15 TIMESTAMP    */   { _, _, _, _, _, _, X, X, _, _, _, _, _, X, X, X, _ },
/* 16 BLOB         */   { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, X },

    };

    /**
     * Table 147 describes  Data Type Compatibility for Assignments 
     *
     * The table 147a covers the assignments as they do differ somewhat 
     *  from comparisons which can be found in 147b
     *
     **/

    public static final boolean[][]  T_147a = {
            
//Types.                S  I  B  D  R  D  C  V  L  C  V  L  C  D  T  T  B
//                   M  N  I  E  E  O  H  A  O  H  A  O  L  A  I  I  L
//                   A  T  G  C  A  U  A  R  N  A  R  N  O  T  M  M  O
//                   L  E  I  I  L  B  R  C  G  R  C  G  B  E  E  E  B
//                   L  G  N  M     L     H  V  .  H  V           S
//                   I  E  T  A     E     A  A  B  .  A           T
//                   N  R     L           R  R  I  B  R           A
//                   T                       C  T  I  .           M
//                                           H     T  B           P
//                                           A        I
//                                              R        T
/* 0 SMALLINT */        { X, X, X, X, X, X, _, _, _, _, _, _, _, _, _, _, _ },
/* 1 INTEGER  */        { X, X, X, X, X, X, _, _, _, _, _, _, _, _, _, _, _ },
/* 2 BIGINT   */        { X, X, X, X, X, X, _, _, _, _, _, _, _, _, _, _, _ },
/* 3 DECIMAL  */        { X, X, X, X, X, X, _, _, _, _, _, _, _, _, _, _, _ },
/* 4 REAL     */        { X, X, X, X, X, X, _, _, _, _, _, _, _, _, _, _, _ },
/* 5 DOUBLE   */        { X, X, X, X, X, X, _, _, _, _, _, _, _, _, _, _, _ },
/* 6 CHAR     */        { _, _, _, _, _, _, X, X, X, _, _, _, X, X, X, X, _ },
/* 7 VARCHAR  */        { _, _, _, _, _, _, X, X, X, _, _, _, X, X, X, X, _ },
/* 8 LONGVARCHAR */     { _, _, _, _, _, _, X, X, X, _, _, _, X, _, _, _, _ },
/* 9 CHAR FOR BIT */    { _, _, _, _, _, _, _, _, _, X, X, X, _, _, _, _, _ },
/* 10 VARCH. BIT   */   { _, _, _, _, _, _, _, _, _, X, X, X, _, _, _, _, _ },
/* 11 LONGVAR. BIT */   { _, _, _, _, _, _, _, _, _, X, X, X, _, _, _, _, _ },
/* 12 CLOB         */   { _, _, _, _, _, _, X, X, X, _, _, _, X, _, _, _, _ },
/* 13 DATE         */   { _, _, _, _, _, _, X, X, _, _, _, _, _, X, _, _, _ },
/* 14 TIME         */   { _, _, _, _, _, _, X, X, _, _, _, _, _, _, X, _, _ },
/* 15 TIMESTAMP    */   { _, _, _, _, _, _, X, X, _, _, _, _, _, _, _, X, _ },
/* 16 BLOB         */   { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, X },

    };


    // Comparisons table
    // Comparison's are different than assignments because
    // Long types cannot be compared.
    public static final boolean[][]  T_147b = {
            
//Types.                 S  I  B  D  R  D  C  V  L  C  V  L  C  D  T  T  B
//                    M  N  I  E  E  O  H  A  O  H  A  O  L  A  I  I  L
//                    A  T  G  C  A  U  A  R  N  A  R  N  O  T  M  M  O
//                    L  E  I  I  L  B  R  C  G  R  C  G  B  E  E  E  B
//                    L  G  N  M     L     H  V  .  H  V           S
//                    I  E  T  A     E     A  A  B  .  A           T
//                    N  R     L           R  R  I  B  R           A
//                    T                       C  T  I  .           M
//                                            H     T  B           P
//                                            A        I
//                                            R        T
/* 0 SMALLINT */        { X, X, X, X, X, X, _, _, _, _, _, _, _, _, _, _, _ },
/* 1 INTEGER  */        { X, X, X, X, X, X, _, _, _, _, _, _, _, _, _, _, _ },
/* 2 BIGINT   */        { X, X, X, X, X, X, _, _, _, _, _, _, _, _, _, _, _ },
/* 3 DECIMAL  */        { X, X, X, X, X, X, _, _, _, _, _, _, _, _, _, _, _ },
/* 4 REAL     */        { X, X, X, X, X, X, _, _, _, _, _, _, _, _, _, _, _ },
/* 5 DOUBLE   */        { X, X, X, X, X, X, _, _, _, _, _, _, _, _, _, _, _ },
/* 6 CHAR     */        { _, _, _, _, _, _, X, X, _, _, _, _, _, X, X, X, _ },
/* 7 VARCHAR  */        { _, _, _, _, _, _, X, X, _, _, _, _, _, X, X, X, _ },
/* 8 LONGVARCHAR */     { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ },
/* 9 CHAR FOR BIT */    { _, _, _, _, _, _, _, _, _, X, X, _, _, _, _, _, _ },
/* 10 VARCH. BIT   */   { _, _, _, _, _, _, _, _, _, X, X, _, _, _, _, _, _ },
/* 11 LONGVAR. BIT */   { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ },
/* 12 CLOB         */   { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ },
/* 13 DATE         */   { _, _, _, _, _, _, X, X, _, _, _, _, _, X, _, _, _ },
/* 14 TIME         */   { _, _, _, _, _, _, X, X, _, _, _, _, _, _, X, _, _ },
/* 15 TIMESTAMP    */   { _, _, _, _, _, _, X, X, _, _, _, _, _, _, _, X, _ },
/* 16 BLOB         */   { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ },


};

    protected void setUp() throws SQLException {
        Connection conn = getConnection();
        Statement scb = conn.createStatement();

        for (int type = 0; type < SQLTypes.length; type++) {
            String typeName = SQLTypes[type];
            String tableName = getTableName(type);

            String createSQL = "create table " + tableName + " (c " + typeName
                    + " )";

            scb.executeUpdate(createSQL);
        }

        scb.close();
        conn.commit();
    }

    public void testAssignments() throws SQLException {

        Connection conn = getConnection();

        Statement scb = conn.createStatement();
        ResultSet rs = null;

        // * testing literal inserts

        for (int dataOffset = 0; dataOffset < SQLData[0].length; dataOffset++)
            for (int type = 0; type < SQLTypes.length; type++) {
                try {
                    String tableName = getTableName(type);

                    String insertSQL = "insert into " + tableName + " values( "
                            + SQLData[type][dataOffset] + ")";
                    scb.executeUpdate(insertSQL);
                } catch (SQLException se) {
                    // literal inserts are ok for everything but BLOB
                    if (type != BLOB_OFFSET)
                        throw se;
                    
                }
            }
        // Try to insert each sourceType into the targetType table
        for (int dataOffset = 0; dataOffset < SQLData[0].length; dataOffset++)
            for (int sourceType = 0; sourceType < SQLTypes.length; sourceType++) {
                String sourceTypeName = SQLTypes[sourceType];
                for (int targetType = 0; targetType < SQLTypes.length; targetType++) {
                    try {
                        String convertString = null;
                        String targetTableName = getTableName(targetType);

                        // For assignments Character types use strings that can
                        // be converted to the targetType.
                        convertString = getCompatibleString(sourceType,
                                targetType, dataOffset);

                        String insertValuesString = " VALUES CAST("
                                + convertString + " AS " + sourceTypeName + ")";

                        String insertSQL = "INSERT INTO " + targetTableName
                                + insertValuesString;
                        // System.out.println(insertSQL);
                        scb.executeUpdate(insertSQL);
                        checkSupportedAssignment(sourceType, targetType);

                    } catch (SQLException se) {
                        String sqlState = se.getSQLState();
                        assertTrue(!isSupportedAssignment(sourceType, targetType)
                                && isNotStorableException(se)
                                || isCastException(se));
                    }
                }
            }

        scb.close();
        conn.commit();
        conn.close();

    }

    public void testExplicitCasts() throws SQLException {

        Connection conn = getConnection();
        Statement s = conn.createStatement();
        ResultSet rs = null;

        // Try Casts from each type to the
        for (int sourceType = 0; sourceType < SQLTypes.length; sourceType++) {

            String sourceTypeName = SQLTypes[sourceType];
            for (int dataOffset = 0; dataOffset < SQLData[0].length; dataOffset++)
                for (int targetType = 0; targetType < SQLTypes.length; targetType++) {
                    String query = null;
                    try {
                        String convertString = null;
                        String targetTypeName = SQLTypes[targetType];
                        // For casts from Character types use strings that can
                        // be converted to the targetType.

                        convertString = getCompatibleString(sourceType,
                                targetType, dataOffset);

                        query = "VALUES CAST (CAST (" + convertString + " AS "
                                + SQLTypes[sourceType] + ") AS "
                                + SQLTypes[targetType] + " )";
                        rs = s.executeQuery(query);
                        JDBC.assertDrainResults(rs);
                        checkSupportedCast(sourceType, targetType);
                    } catch (SQLException se) {
                        String sqlState = se.getSQLState();
                        if (!isSupportedCast(sourceType, targetType)) {
                            assertTrue(isCastException(se));
                        } else
                            throw se;
                    }
                }
        }

        conn.commit();

    }

    public void testComparisons() throws SQLException {

        Connection conn = getConnection();
        Statement scb = conn.createStatement();
        ResultSet rs = null;

        // Comparison's using literals

        for (int type = 0; type < SQLTypes.length; type++) {
            try {
                int dataOffset = 1; // don't use null values
                String tableName = getTableName(type);

                String compareSQL = "SELECT distinct c FROM " + tableName
                        + " WHERE c = " + SQLData[type][dataOffset];

                rs = scb.executeQuery(compareSQL);
                JDBC.assertDrainResults(rs);
            } catch (SQLException se) {
                // literal comparisons are ok for everything but Lob and long
                assertTrue(isLongType(type));
            }
        }

        // Try to compare each sourceType with the targetType
        for (int dataOffset = 0; dataOffset < SQLData[0].length; dataOffset++)
            for (int sourceType = 0; sourceType < SQLTypes.length; sourceType++) {
                String sourceTypeName = SQLTypes[sourceType];
                for (int targetType = 0; targetType < SQLTypes.length; targetType++) {
                    try {
                        String convertString = null;
                        String targetTableName = getTableName(targetType);

                        // For assignments Character types use strings that can
                        // be converted to the targetType.
                        convertString = getCompatibleString(sourceType,
                                targetType, dataOffset);

                        // Make sure table has just compatible data
                        scb.executeUpdate("DELETE FROM " + targetTableName);
                        String insertValuesString = " VALUES CAST("
                                + convertString + " AS " + sourceTypeName + ")";

                        String insertSQL = "INSERT INTO " + targetTableName
                                + insertValuesString;

                        String compareSQL = "select c from " + targetTableName
                                + " WHERE c = CAST(" + convertString + " AS "
                                + sourceTypeName + ")";

                        // System.out.println(compareSQL);
                        rs = scb.executeQuery(compareSQL);
                        JDBC.assertDrainResults(rs);
                        // JDBCDisplayUtil.DisplayResults(System.out,rs,conn);
                        checkSupportedComparison(sourceType, targetType);

                    } catch (SQLException se) {
                        String sqlState = se.getSQLState();
                        assertTrue(!isSupportedComparison(sourceType, targetType)
                                && isNotComparableException(se)
                                || isCastException(se));
                       
                    }
                }
            }
        scb.close();
        conn.commit();

    }

    protected void tearDown() throws SQLException, Exception {
        super.tearDown();
        Connection conn = getConnection();
        Statement scb = conn.createStatement();

        for (int type = 0; type < SQLTypes.length; type++) {
            String typeName = SQLTypes[type];
            String tableName = getTableName(type);

            String dropSQL = "drop table " + tableName;

            scb.executeUpdate(dropSQL);
        }

        scb.close();
        conn.commit();
        conn.close();
    }

    /**
     * Build a unique table name from the type
     * 
     * @param type
     *            table offset
     * @return Table name in format <TYPE>_TAB. Replaces ' ' _;
     */
    private static String getTableName(int type) {
        return getShortTypeName(type).replace(' ', '_') + "_TAB";

    }

    /**
     * Truncates (*) from typename
     * 
     * @param type -
     *            Type offset
     * 
     * @return short name of type (e.g DECIMAL instead of DECIMAL(10,5)
     */

    private static String getShortTypeName(int type) {
        String typeName = SQLTypes[type];
        String shortName = typeName;
        int parenIndex = typeName.indexOf('(');
        if (parenIndex >= 0) {
            shortName = typeName.substring(0, parenIndex);
            int endParenIndex = typeName.indexOf(')');
            shortName = shortName
                    + typeName.substring(endParenIndex + 1, typeName.length());
        }
        return shortName;

    }

    private static String getCompatibleString(int sourceType, int targetType,
            int dataOffset) {
        String convertString = null;
        // for string and binary types use the target data string
        // so that the cast will work
        if ((isCharacterType(sourceType) || isBinaryType(sourceType))
                && !isLob(sourceType))
            convertString = formatString(SQLData[targetType][dataOffset]);
        else
            convertString = SQLData[sourceType][dataOffset];

        return convertString;
    }

    private static boolean isSupportedCast(int sourceType, int targetType) {
        return T_146[sourceType][targetType];
    }

    private static boolean isSupportedAssignment(int sourceType, int targetType) {
        return T_147a[sourceType][targetType];
    }

    private static boolean isSupportedComparison(int sourceType, int targetType) {
        return T_147b[sourceType][targetType];
    }

    private static boolean isCastException(SQLException se) {
        return sqlStateMatches(se, ILLEGAL_CAST_EXCEPTION_SQLSTATE);
    }

    private static boolean isMethodNotFoundException(SQLException se) {
        return sqlStateMatches(se, METHOD_NOT_FOUND_SQLSTATE);
    }

    private static boolean sqlStateMatches(SQLException se, String expectedValue) {
        String sqlState = se.getSQLState();
        if ((sqlState != null) && (sqlState.equals(expectedValue)))
            return true;
        return false;
    }

    private static boolean isNotStorableException(SQLException se) {
        String sqlState = se.getSQLState();
        if ((sqlState != null) && (sqlState.equals(LANG_NOT_STORABLE_SQLSTATE)))
            return true;
        return false;

    }

    private static boolean isNotComparableException(SQLException se) {
        String sqlState = se.getSQLState();
        if ((sqlState != null)
                && (sqlState.equals(LANG_NOT_COMPARABLE_SQLSTATE)))
            return true;
        return false;
    }

    private static void checkSupportedCast(int sourceType, int targetType) {
        String description = " Cast from " + SQLTypes[sourceType] + " to "
                + SQLTypes[targetType];

        if (!isSupportedCast(sourceType, targetType))
            fail(description + "should not succeed");
    }

    private static void checkSupportedAssignment(int sourceType, int targetType) {
        String description = " Assignment from " + SQLTypes[sourceType]
                + " to " + SQLTypes[targetType];

        if (!isSupportedAssignment(sourceType, targetType))
            fail(description + "should not succeed");

    }

    private static void checkSupportedComparison(int sourceType, int targetType) {
        String description = " Comparison of " + SQLTypes[sourceType] + " to "
                + SQLTypes[targetType];

        if (!isSupportedComparison(sourceType, targetType))
            fail("FAIL: unsupported comparison:" + description);
    }

    private static boolean isLongType(int typeOffset) {
        return ((typeOffset == LONGVARCHAR_OFFSET)
                || (typeOffset == LONGVARCHAR_FOR_BIT_OFFSET)
                || (typeOffset == CLOB_OFFSET) || (typeOffset == BLOB_OFFSET));
    }

    private static boolean isCharacterType(int typeOffset) {
        return ((typeOffset == CHAR_OFFSET) || (typeOffset == VARCHAR_OFFSET)
                || (typeOffset == LONGVARCHAR_OFFSET) || (typeOffset == CLOB_OFFSET));
    }

    private static boolean isBinaryType(int typeOffset) {
        return ((typeOffset == CHAR_FOR_BIT_OFFSET)
                || (typeOffset == VARCHAR_FOR_BIT_OFFSET)
                || (typeOffset == LONGVARCHAR_FOR_BIT_OFFSET) || (typeOffset == BLOB_OFFSET));
    }

    private static boolean isDateTimeTimestamp(int typeOffset) {
        return ((typeOffset == DATE_OFFSET) || (typeOffset == TIME_OFFSET) || (typeOffset == TIMESTAMP_OFFSET));

    }

    private static boolean isClob(int typeOffset) {
        return (typeOffset == CLOB_OFFSET);
    }

    private static boolean isLob(int typeOffset) {
        return ((typeOffset == CLOB_OFFSET) || (typeOffset == BLOB_OFFSET));

    }

    // Data is already a string (starts with X, or a character string,
    // just return, otherwise bracket with ''s
    private static String formatString(String str) {
        if ((str != null)
                && (str.startsWith("X") || str.startsWith("'") || (str == NULL_VALUE)))
            return str;
        else
            return "'" + str + "'";
    }

    private static Test suite() {

        return TestConfiguration.defaultSuite(CastingTest.class);

    }
}
