/*
 * Copyright (c) 2025, Oracle and/or its affiliates.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License, version 2.0, as published by
 * the Free Software Foundation.
 *
 * This program is designed to work with certain software that is licensed under separate terms, as designated in a particular file or component or in
 * included license documentation. The authors of MySQL hereby grant you an additional permission to link the program and your derivative works with the
 * separately licensed software that they have either included with the program or referenced in the documentation.
 *
 * Without limiting anything contained in the foregoing, this file, which is part of MySQL Connector/J, is also subject to the Universal FOSS Exception,
 * version 1.0, a copy of which can be found at http://oss.oracle.com/licenses/universal-foss-exception.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License, version 2.0, for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package com.mysql.cj.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mysql.cj.Messages;
import com.mysql.cj.MysqlType;
import com.mysql.cj.exceptions.AssertionFailedException;
import com.mysql.cj.exceptions.MysqlErrorNumbers;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.result.ResultSetFactory;
import com.mysql.cj.protocol.a.result.ByteArrayRow;
import com.mysql.cj.protocol.a.result.ResultsetRowsStatic;
import com.mysql.cj.result.DefaultColumnDefinition;
import com.mysql.cj.result.Field;
import com.mysql.cj.result.Row;
import com.mysql.cj.util.SearchMode;
import com.mysql.cj.util.StringInspector;
import com.mysql.cj.util.StringUtils;

/**
 * DatabaseMetaData implementation that uses MYSQL schema tables and SHOW commands.
 */
public class DatabaseMetaDataMysqlSchema extends DatabaseMetaData {

    /**
     * Enumeration for stored routine Types. The order matters for sorting stored routines elements.
     */
    private enum StoredRoutineType {
        FUNCTION, PROCEDURE;
    }

    /**
     * Parses and represents common data type information used by various column/parameter methods.
     */
    private class TypeDescriptor {

        int bufferLength;
        Integer datetimePrecision = null;
        Integer columnSize = null;
        Integer charOctetLength = null;
        Integer decimalDigits = null;
        String isNullable;
        int nullability;
        int numPrecRadix = 10;
        MysqlType mysqlType;

        TypeDescriptor(String typeInfo, String nullabilityInfo) throws SQLException {
            if (typeInfo == null) {
                throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.0"), MysqlErrorNumbers.SQLSTATE_CONNJ_ILLEGAL_ARGUMENT,
                        getExceptionInterceptor());
            }

            this.mysqlType = MysqlType.getByName(typeInfo);

            String temp;
            java.util.StringTokenizer tokenizer;
            int maxLength = 0;
            int fract;

            switch (this.mysqlType) {
                case ENUM:
                    temp = typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.lastIndexOf(")"));
                    tokenizer = new java.util.StringTokenizer(temp, ",");
                    while (tokenizer.hasMoreTokens()) {
                        String nextToken = tokenizer.nextToken();
                        maxLength = Math.max(maxLength, nextToken.length() - 2);
                    }
                    this.columnSize = maxLength;
                    break;

                case SET:
                    temp = typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.lastIndexOf(")"));
                    tokenizer = new java.util.StringTokenizer(temp, ",");
                    int numElements = tokenizer.countTokens();
                    if (numElements > 0) {
                        maxLength += numElements - 1;
                    }
                    while (tokenizer.hasMoreTokens()) {
                        String setMember = tokenizer.nextToken().trim();

                        if (setMember.startsWith("'") && setMember.endsWith("'")) {
                            maxLength += setMember.length() - 2;
                        } else {
                            maxLength += setMember.length();
                        }
                    }
                    this.columnSize = maxLength;
                    break;

                case FLOAT:
                case FLOAT_UNSIGNED:
                    if (typeInfo.indexOf(",") != -1) {
                        // Numeric with decimals.
                        this.columnSize = Integer.valueOf(typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.indexOf(",")).trim());
                        this.decimalDigits = Integer.valueOf(typeInfo.substring(typeInfo.indexOf(",") + 1, typeInfo.indexOf(")")).trim());
                    } else if (typeInfo.indexOf("(") != -1) {
                        int size = Integer.parseInt(typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.indexOf(")")).trim());
                        if (size > 23) {
                            this.mysqlType = this.mysqlType == MysqlType.FLOAT ? MysqlType.DOUBLE : MysqlType.DOUBLE_UNSIGNED;
                            this.columnSize = 22;
                            this.decimalDigits = 0;
                        }
                    } else {
                        this.columnSize = 12;
                        this.decimalDigits = 0;
                    }
                    break;

                case DECIMAL:
                case DECIMAL_UNSIGNED:
                case DOUBLE:
                case DOUBLE_UNSIGNED:
                    if (typeInfo.indexOf(",") != -1) {
                        // Numeric with decimals.
                        this.columnSize = Integer.valueOf(typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.indexOf(",")).trim());
                        this.decimalDigits = Integer.valueOf(typeInfo.substring(typeInfo.indexOf(",") + 1, typeInfo.indexOf(")")).trim());
                    } else {
                        switch (this.mysqlType) {
                            case DECIMAL:
                            case DECIMAL_UNSIGNED:
                                this.columnSize = 65;
                                break;
                            case DOUBLE:
                            case DOUBLE_UNSIGNED:
                                this.columnSize = 22;
                                break;
                            default:
                                break;
                        }
                        this.decimalDigits = 0;
                    }
                    break;

                case CHAR:
                case VARCHAR:
                case TINYTEXT:
                case MEDIUMTEXT:
                case LONGTEXT:
                case JSON:
                case TEXT:
                case TINYBLOB:
                case MEDIUMBLOB:
                case LONGBLOB:
                case BLOB:
                case BINARY:
                case VARBINARY:
                case BIT:
                    if (this.mysqlType == MysqlType.CHAR) {
                        this.columnSize = 1;
                    }
                    if (typeInfo.indexOf("(") != -1) {
                        int endParenIndex = typeInfo.indexOf(")");
                        if (endParenIndex == -1) {
                            endParenIndex = typeInfo.length();
                        }
                        this.columnSize = Integer.valueOf(typeInfo.substring(typeInfo.indexOf("(") + 1, endParenIndex).trim());
                        // Adjust for pseudo-boolean.
                        if (tinyInt1IsBitValue() && this.columnSize.intValue() == 1 && StringUtils.startsWithIgnoreCase(typeInfo, "tinyint")) {
                            if (transformedBitIsBooleanValue()) {
                                this.mysqlType = MysqlType.BOOLEAN;
                            } else {
                                this.mysqlType = MysqlType.BIT;
                            }
                        }
                    }
                    break;

                case TINYINT:
                    if (tinyInt1IsBitValue() && typeInfo.indexOf("(1)") != -1) {
                        if (transformedBitIsBooleanValue()) {
                            this.mysqlType = MysqlType.BOOLEAN;
                        } else {
                            this.mysqlType = MysqlType.BIT;
                        }
                    } else {
                        this.columnSize = 3;
                    }
                    break;

                case TINYINT_UNSIGNED:
                    this.columnSize = 3;
                    break;

                case DATE:
                    this.datetimePrecision = 0;
                    this.columnSize = 10;
                    break;

                case TIME:
                    this.datetimePrecision = 0;
                    this.columnSize = 8;
                    if (typeInfo.indexOf("(") != -1
                            && (fract = Integer.parseInt(typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.indexOf(")")).trim())) > 0) {
                        // With fractional seconds.
                        this.datetimePrecision = fract;
                        this.columnSize += fract + 1;
                    }
                    break;

                case DATETIME:
                case TIMESTAMP:
                    this.datetimePrecision = 0;
                    this.columnSize = 19;
                    if (typeInfo.indexOf("(") != -1
                            && (fract = Integer.parseInt(typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.indexOf(")")).trim())) > 0) {
                        // With fractional seconds.
                        this.datetimePrecision = fract;
                        this.columnSize += fract + 1;
                    }
                    break;

                case BOOLEAN:
                case GEOMETRY:
                case NULL:
                case UNKNOWN:
                case YEAR:

                default:
            }

            // If not defined explicitly take the max precision.
            if (this.columnSize == null) {
                // JDBC spec reserved only 'int' type for precision, thus longer values need to be cut.
                this.columnSize = this.mysqlType.getPrecision() > Integer.MAX_VALUE ? Integer.MAX_VALUE : this.mysqlType.getPrecision().intValue();
            }

            switch (this.mysqlType) {
                case CHAR:
                case VARCHAR:
                case TINYTEXT:
                case MEDIUMTEXT:
                case LONGTEXT:
                case JSON:
                case TEXT:
                case TINYBLOB:
                case MEDIUMBLOB:
                case LONGBLOB:
                case BLOB:
                case BINARY:
                case VARBINARY:
                case BIT:
                    this.charOctetLength = this.columnSize;
                    break;
                default:
                    break;
            }

            // BUFFER_LENGTH
            this.bufferLength = MAX_BUFFER_SIZE;

            // NUM_PREC_RADIX (is this right for char?)
            this.numPrecRadix = 10;

            // IS_NULLABLE
            if (nullabilityInfo != null) {
                if (nullabilityInfo.equals("YES")) {
                    this.nullability = columnNullable;
                    this.isNullable = "YES";
                } else if (nullabilityInfo.equals("UNKNOWN")) {
                    this.nullability = columnNullableUnknown;
                    this.isNullable = "";
                } else {
                    this.nullability = columnNoNulls;
                    this.isNullable = "NO";
                }
            } else {
                this.nullability = columnNoNulls;
                this.isNullable = "NO";
            }
        }

        int getJdbcType() {
            return this.mysqlType == MysqlType.YEAR && !yearIsDateTypeValue() ? Types.SMALLINT : this.mysqlType.getJdbcType();
        }

    }

    /**
     * Helper class that represents a composite object that is comparable based on the lexicographic comparison of its elements. The comparison is performed in
     * the order the elements are supplied, following the rules of {@link Comparable#compareTo(Object)}.
     */
    private class MultiComparable implements Comparable<MultiComparable> {

        private final List<Comparable<?>> elements;

        public MultiComparable(Comparable<?>... elements) {
            this.elements = Arrays.asList(elements);
        }

        @SuppressWarnings("unchecked")
        public <T> T getElement(int index) {
            if (index < 0 || index >= this.elements.size()) {
                throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + this.elements.size());
            }
            return (T) this.elements.get(index);
        }

        @Override
        public int compareTo(MultiComparable other) {
            int size = Math.min(this.elements.size(), other.elements.size());
            for (int i = 0; i < size; i++) {
                Comparable<?> thisElement = this.elements.get(i);
                Comparable<?> otherElement = other.elements.get(i);

                if (thisElement == null && otherElement == null) {
                    continue;
                }
                if (thisElement == null) {
                    return -1;
                }
                if (otherElement == null) {
                    return 1;
                }

                @SuppressWarnings("unchecked")
                int result = ((Comparable<Object>) thisElement).compareTo(otherElement);
                if (result != 0) {
                    return result;
                }
            }
            // If all compared elements are equal, compare based on size.
            return Integer.compare(this.elements.size(), other.elements.size());
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            MultiComparable other = (MultiComparable) obj;
            return Objects.equals(this.elements, other.elements);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.elements);
        }

        @Override
        public String toString() {
            return this.elements.toString();
        }

    }

    DatabaseMetaDataMysqlSchema(JdbcConnection connToSet, String databaseToSet, ResultSetFactory resultSetFactory) {
        super(connToSet, databaseToSet, resultSetFactory);
    }

    /**
     * Structure for holding foreign key constraints information.
     */
    private class ForeignKeyConstraintInfo {

        final String constraintName;
        final String referencingDatabase;
        final String referencingTable;
        final List<String> referencingColumnsList;
        final String referencedDatabase;
        final String referencedTable;
        final List<String> referencedColumnsList;
        final int referentialActionOnDelete;
        final int referentialActionOnUpdate;

        ForeignKeyConstraintInfo(String constraintName, String referencingDatabase, String referencingTable, List<String> referencingColumns,
                                 String referencedDatabase, String referencedTable, List<String> referencedColumns, int referentialActionOnDelete,
                                 int referentialActionOnUpdate) {
            this.constraintName = constraintName;
            this.referencingDatabase = referencingDatabase;
            this.referencingTable = referencingTable;
            this.referencingColumnsList = referencingColumns;
            this.referencedColumnsList = referencedColumns;
            this.referencedTable = referencedTable;
            this.referencedDatabase = referencedDatabase;
            this.referentialActionOnDelete = referentialActionOnDelete;
            this.referentialActionOnUpdate = referentialActionOnUpdate;
        }

    }

    /**
     * Extracts the foreign key constraints information from the specified table or from all the tables from the specified database.
     *
     * @param dbName
     *            The database name to fetch the tables from.
     * @param tableName
     *            The table to fetch. If {@code null} then all tables from the database are fetched.
     * @return
     *              A list with all foreign key constraints information.
     * @throws SQLException
     *             If an error occurs.
     */
    private List<ForeignKeyConstraintInfo> extractForeignKeysForTable(String dbName, String tableName) throws SQLException {
        List<String> tableList = new ArrayList<>();
        if (tableName != null) {
            tableList.add(tableName);
        } else {
            ResultSet rs = null;
            try {
                // If not pedantic mode then quote database name before calling #getTables().
                String quotedDbName = pedanticValue() ? dbName : StringUtils.quoteIdentifier(dbName, getQuoteId(), true);
                rs = chooseBasedOnDatabaseTerm(() -> getTables(quotedDbName, null, null, new String[] { "TABLE" }),
                        () -> getTables(null, quotedDbName, null, new String[] { "TABLE" }));
                while (rs.next()) {
                    tableList.add(rs.getString("TABLE_NAME"));
                }
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        AssertionFailedException.shouldNotHappen(e);
                    }
                    rs = null;
                }
            }
        }

        List<ForeignKeyConstraintInfo> tableForeignKeysList = new ArrayList<>();
        ResultSet rs = null;
        try (Statement stmt = getJdbcConnection().getMetaDataSafeStatement()) {
            for (String tableToExtract : tableList) {
                StringBuilder query = new StringBuilder("SHOW CREATE TABLE ");
                query.append(StringUtils.getFullyQualifiedName(dbName, tableToExtract, getQuoteId(), true));
                try {
                    rs = stmt.executeQuery(query.toString());
                } catch (SQLException e) {
                    String sqlState = e.getSQLState(); // Ignore exception if SQLState is 42S02 or 42000 - table/database doesn't exist.
                    int errorCode = e.getErrorCode(); // Ignore exception if ErrorCode is 1146, 1109, or 1149 - table/database doesn't exist.
                    if (!(MysqlErrorNumbers.SQLSTATE_MYSQL_BASE_TABLE_OR_VIEW_NOT_FOUND.equals(sqlState)
                            && (errorCode == MysqlErrorNumbers.ER_NO_SUCH_TABLE || errorCode == MysqlErrorNumbers.ER_UNKNOWN_TABLE)
                            || MysqlErrorNumbers.SQLSTATE_SYNTAX_ERROR_OR_ACCESS_RULE_VIOLATION_NO_SUBCLASS.equals(sqlState)
                            && errorCode == MysqlErrorNumbers.ER_BAD_DB_ERROR)) {
                        throw e;
                    }
                    continue;
                }

                while (rs.next()) {
                    tableForeignKeysList.addAll(extractForeignKeysFromCreateTable(dbName, rs.getString(1), rs.getString(2)));
                }
            }
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    AssertionFailedException.shouldNotHappen(e);
                }
                rs = null;
            }
        }

        return tableForeignKeysList;
    }

    /**
     * Extracts the foreign key constraints information from the specified create table command, parsing the following syntax:
     * <br/>
     *      "CONSTRAINT `<constraint_name>` FOREIGN KEY (`<referencing_col_1>`, `<referencing_col_2>`, ...) REFERENCES `<referenced_table>`
     * (`<referenced_col_1>`, `<referenced_col_2>`, ...) ON DELETE <action> ON UPDATE <action>"
     *
     * @param dbName
     *            The referencing database.
     * @param tableName
     *            The referencing table.
     * @param createTableSql
     *            The CREATE TABLE sql for the referencing table.
     * @return
     *              A list with all foreign key constraints information.
     * @throws SQLException
     *             If an error occurs while parsing the foreign key constraints SQL.
     */
    private List<ForeignKeyConstraintInfo> extractForeignKeysFromCreateTable(String dbName, String tableName, String createTableSql) throws SQLException {
        Function<String, Integer> mysqlActionToJdbc = action -> {
            if (action.startsWith("NO ACTION")) {
                return importedKeyNoAction;
            } else if (action.startsWith("CASCADE")) {
                return importedKeyCascade;
            } else if (action.startsWith("SET NULL")) {
                return importedKeySetNull;
            } else if (action.startsWith("SET DEFAULT")) {
                return importedKeySetDefault;
            }
            return importedKeyRestrict; // RESTRICT
        };
        List<ForeignKeyConstraintInfo> tableForeignKeysList = new ArrayList<>();

        StringTokenizer lineTokenizer = new StringTokenizer(createTableSql, "\n");
        while (lineTokenizer.hasMoreTokens()) {
            String line = lineTokenizer.nextToken().trim();
            String constraintName = null;
            List<String> referencingColumnNames;
            String referencedDbName = StringUtils.quoteIdentifier(dbName, getQuoteId(), true);
            String referencedTableName = "";
            List<String> referencedColumnNames;
            int referentialActionOnDelete = importedKeyRestrict;
            int referentialActionOnUpdate = importedKeyRestrict;

            if (StringUtils.startsWithIgnoreCase(line, "CONSTRAINT")) {
                int beginPos = line.indexOf(getQuoteId());
                if (beginPos != -1) {
                    int endPos = -1;
                    endPos = StringUtils.indexOfQuoteDoubleAware(line, getQuoteId(), beginPos + 1);
                    if (endPos != -1) {
                        constraintName = StringUtils.unquoteIdentifier(line.substring(beginPos + 1, endPos), getQuoteId());
                        line = line.substring(endPos + 1, line.length()).trim();
                    }
                }
            }
            if (line.startsWith("FOREIGN KEY")) {
                int afterFk = "FOREIGN KEY".length();
                int referencingColumnsBegin = StringUtils.indexOfIgnoreCase(afterFk, line, "(", getQuoteId(), getQuoteId(),
                        SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
                if (referencingColumnsBegin == -1) {
                    throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.14"), MysqlErrorNumbers.SQLSTATE_CONNJ_GENERAL_ERROR,
                            getExceptionInterceptor());
                }
                int referencingColumnsEnd = StringUtils.indexOfIgnoreCase(referencingColumnsBegin, line, ")", getQuoteId(), getQuoteId(),
                        SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
                if (referencingColumnsEnd == -1) {
                    throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.15"), MysqlErrorNumbers.SQLSTATE_CONNJ_GENERAL_ERROR,
                            getExceptionInterceptor());
                }
                String referencingColumnNamesToken = line.substring(referencingColumnsBegin + 1, referencingColumnsEnd);
                referencingColumnNames = StringUtils.split(referencingColumnNamesToken, ",", getQuoteId(), getQuoteId(), false).stream()
                        .map(c -> StringUtils.unquoteIdentifier(c, getQuoteId())).collect(Collectors.toList());

                int indexOfRef = StringUtils.indexOfIgnoreCase(afterFk, line, "REFERENCES", getQuoteId(), getQuoteId(), SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
                if (indexOfRef == -1) {
                    throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.16"), MysqlErrorNumbers.SQLSTATE_CONNJ_GENERAL_ERROR,
                            getExceptionInterceptor());
                }
                int afterRef = indexOfRef + "REFERENCES".length();
                int referencedColumnsBegin = StringUtils.indexOfIgnoreCase(afterRef, line, "(", getQuoteId(), getQuoteId(),
                        SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
                if (referencedColumnsBegin == -1) {
                    throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.17"), MysqlErrorNumbers.SQLSTATE_CONNJ_GENERAL_ERROR,
                            getExceptionInterceptor());
                }
                referencedTableName = line.substring(afterRef, referencedColumnsBegin).trim();
                int referencedColumnsEnd = StringUtils.indexOfIgnoreCase(referencedColumnsBegin + 1, line, ")", getQuoteId(), getQuoteId(),
                        SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
                if (referencedColumnsEnd == -1) {
                    throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.18"), MysqlErrorNumbers.SQLSTATE_CONNJ_GENERAL_ERROR,
                            getExceptionInterceptor());
                }
                String referencedColumnNamesToken = line.substring(referencedColumnsBegin + 1, referencedColumnsEnd);
                referencedColumnNames = StringUtils.split(referencedColumnNamesToken, ",", getQuoteId(), getQuoteId(), false).stream()
                        .map(c -> StringUtils.unquoteIdentifier(c, getQuoteId())).collect(Collectors.toList());
                if (referencedColumnNames.size() != referencingColumnNames.size()) {
                    throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.12"), MysqlErrorNumbers.SQLSTATE_CONNJ_GENERAL_ERROR,
                            getExceptionInterceptor());
                }

                List<String> tableRef = StringUtils.splitDBdotName(referencedTableName, referencedDbName, getQuoteId(), true);
                referencedDbName = StringUtils.unquoteIdentifier(tableRef.get(0), getQuoteId());
                referencedTableName = StringUtils.unquoteIdentifier(tableRef.get(1), getQuoteId());
                String referentialActions = line.substring(referencedColumnsEnd + 1);
                int onDeletePos = referentialActions.indexOf("ON DELETE");
                if (onDeletePos != -1) {
                    int afterOnDelete = onDeletePos + "ON DELETE ".length();
                    String onDeleteAction = referentialActions.substring(afterOnDelete);
                    referentialActionOnDelete = mysqlActionToJdbc.apply(onDeleteAction);
                }
                int onUpdatePos = referentialActions.indexOf("ON UPDATE");
                if (onUpdatePos != -1) {
                    int afterOnUpdate = onUpdatePos + "ON UPDATE ".length();
                    String onUpdateAction = referentialActions.substring(afterOnUpdate);
                    referentialActionOnUpdate = mysqlActionToJdbc.apply(onUpdateAction);
                }

                tableForeignKeysList.add(new ForeignKeyConstraintInfo(constraintName, dbName, tableName, referencingColumnNames, referencedDbName,
                        referencedTableName, referencedColumnNames, referentialActionOnDelete, referentialActionOnUpdate));
            }
        }
        return tableForeignKeysList;
    }

    /**
     * Retrieves stored routines (procedures and/or functions) metadata.
     *
     * @param catalog
     *            The catalog name.
     * @param schemaPattern
     *            The schema pattern.
     * @param routineNamePattern
     *            The procedure name pattern.
     * @param targetMetaData
     *            The type of metadata to produce.
     * @return
     *              The result set containing the metadata. If {@code targetMetada == BOTH} then returns the metadata structure for procedures as it supports
     *         functions too (getFunction* methods were added later).
     * @throws SQLException
     *             If a database access error occurs.
     */
    private ResultSet getStoredRoutines(String catalog, String schemaPattern, String routineNamePattern, StoredRoutineType targetMetaData) throws SQLException {
        final String dbFilter = chooseDatabaseTerm(catalog, schemaPattern);
        final String routineNameFilter = normalizeIdentifierQuoting(routineNamePattern);

        final SortedMap<MultiComparable, Row> sortedRows = new TreeMap<>();
        List<String> dbList = chooseBasedOnDatabaseTerm(() -> getDatabasesByLiteral(dbFilter), () -> getDatabasesByPattern(dbFilter));
        for (String db : dbList) {
            // Collect functions metadata.
            if (targetMetaData == StoredRoutineType.FUNCTION || getProceduresReturnsFunctionsValue()) {
                StringBuilder query = new StringBuilder("SHOW FUNCTION STATUS WHERE ");
                query.append(chooseBasedOnDatabaseTerm(() -> "Db = ?", () -> "Db LIKE ?"));
                if (!StringUtils.isNullOrEmpty(routineNameFilter)) {
                    query.append(" AND Name LIKE ?");
                }
                try (PreparedStatement functionsStmt = prepareMetaDataSafeStatement(query.toString())) {
                    functionsStmt.setString(1, db);
                    if (!StringUtils.isNullOrEmpty(routineNameFilter)) {
                        functionsStmt.setString(2, routineNameFilter);
                    }
                    ResultSet functionsRs = functionsStmt.executeQuery();
                    while (functionsRs.next()) {
                        String funcDb = functionsRs.getString("Db");
                        String functionName = functionsRs.getString("name");
                        byte[][] row = null;
                        if (targetMetaData == StoredRoutineType.PROCEDURE) { // Exposing functions as procedures.
                            row = new byte[9][];
                            row[0] = chooseBasedOnDatabaseTerm(() -> s2b(funcDb), () -> s2b("def"));                        // PROCEDURE_CAT
                            row[1] = chooseBasedOnDatabaseTerm(() -> null, () -> s2b(funcDb));                              // PROCEDURE_SCHEM
                            row[2] = s2b(functionName);                                                                     // PROCEDURE_NAME
                            row[3] = null;                                                                                  // reserved for future use
                            row[4] = null;                                                                                  // reserved for future use
                            row[5] = null;                                                                                  // reserved for future use
                            row[6] = s2b(functionsRs.getString("comment"));                                                 // REMARKS