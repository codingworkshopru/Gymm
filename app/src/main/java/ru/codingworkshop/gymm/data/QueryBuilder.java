package ru.codingworkshop.gymm.data;

import android.provider.BaseColumns;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Радик on 21.02.2017.
 */

public final class QueryBuilder {
    private static final String TAG = QueryBuilder.class.getSimpleName();

    public static String build(List<QueryPart> parts) {
        QueryPart[] partsArray = parts.toArray(new QueryPart[parts.size()]);
        return build(partsArray);
    }


    /**
     * Used to join parts to one SQL query. If the same table is presented in multiple parts aliases will be assigned for 2nd and other tables.
     * For example if table dummy_table presented 3 times following aliases will be generated: dummy_table, dummy_table2, dummy_table3.
     * @param parts parts to join
     * @return SQL query
     */
    public static String build(QueryPart... parts) {
        int partsCount = parts.length;

        StringBuilder columns = new StringBuilder("SELECT ");
        StringBuilder from = new StringBuilder(" FROM ");
        StringBuilder where = new StringBuilder();
        StringBuilder group = new StringBuilder();
        StringBuilder order = new StringBuilder();
        Map<String, Integer> tablesMap = new HashMap<>(partsCount);

        for (int i = 0; i < partsCount; i++) {
            QueryPart part = parts[i];
            String table = part.mTable;
            String alias = table;
            if (tablesMap.containsKey(table)) {
                int tablesCount = tablesMap.get(table);
                alias += ++tablesCount;
                tablesMap.put(table, tablesCount);
            } else {
                tablesMap.put(table, 1);
            }

            if (part.mColumns != null && part.mColumns.length != 0) {
                if (i == 0 && part.mDistinct)
                    columns.append("DISTINCT ");
                columns.append(getColumnsWithTable(part.mColumns, alias)).append(",");
            }

            if (i == 0) {
                from.append(table);
            } else {
                if (part.mThisJoiningColumn == null)
                    throw new IllegalArgumentException("joining column not presented");

                String thisJoinColumn = getColumnWithTable(part.mThisJoiningColumn, alias);
                String otherJoinColumn = getColumnWithTable(
                        part.mOtherJoiningColumn == null ? BaseColumns._ID : part.mOtherJoiningColumn,
                        part.mOtherJoiningTable == null ? parts[i-1].mTable : part.mOtherJoiningTable
                );

                from.append(part.mIsLeftJoin ? " LEFT JOIN " : " JOIN ")
                        .append(table)
                        .append(table.equals(alias) ? "" : " " + alias)
                        .append(" ON ")
                        .append(thisJoinColumn)
                        .append("=")
                        .append(otherJoinColumn);
            }

            if (part.mSelection != null && !part.mSelection.isEmpty()) {
                if (where.length() == 0)
                    where.append(" WHERE ");
                where.append(part.mSelection).append(" AND ");
            }

            if (part.mGroup != null && part.mGroup.length != 0) {
                if (group.length() == 0)
                    group.append(" GROUP BY ");
                group.append(getColumnsWithTable(part.mGroup, alias)).append(",");
            }

            if (part.mOrder != null && part.mOrder.length != 0) {
                if (order.length() == 0)
                    order.append(" ORDER BY ");
                order.append(getColumnsWithTable(part.mOrder, alias)).append(",");
            }
        }

        // if no columns specified
        if (columns.length() == 7)
            columns.append("*");

        deleteLastComma(columns);
        deleteLast(where, "AND");
        deleteLastComma(group);
        deleteLastComma(order);

        String query = columns.toString() + from.toString() + where.toString() + group.toString() + order.toString();

        System.out.println(TAG + " " + query);

        return query;
    }

    private static void deleteLastComma(StringBuilder sb) {
        deleteLast(sb, ",");
    }

    private static void deleteLast(StringBuilder sb, String substring) {
        int lastSubstringIndex = sb.lastIndexOf(substring);
        if (lastSubstringIndex != -1)
            sb.delete(lastSubstringIndex, sb.length());
    }

    private static String getColumnsWithTable(String[] columns, String table) {
        if (columns == null || columns.length == 0)
            return "";

        StringBuilder selectionWithTable = new StringBuilder();
        for (String c : columns) {
            if (c.matches("^\\w+\\(\\w*\\s?\\w+\\)$")) {
                // if a standard SQL function presented
                Pattern pattern = Pattern.compile("(\\w+)\\)$");
                Matcher matcher = pattern.matcher(c);
                if (matcher.find()) {
                    String column = getColumnWithTable(matcher.group(), table);
                    System.out.println(column);
                    selectionWithTable.append(matcher.replaceFirst(column));
                }
            } else {
                // if column name specified
                Pattern pattern = Pattern.compile("\\s+as\\s+", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                Matcher matcher = pattern.matcher(c);
                if (matcher.find()) {
                    selectionWithTable.append(c);
                } else {
                    selectionWithTable.append(getColumnWithTable(c, table));
                }
            }
            selectionWithTable.append(",");
        }
        deleteLastComma(selectionWithTable);
        return selectionWithTable.toString();
    }

    public static String getColumnWithTable(String column, String table) {
        return table + "." + column;
    }

    public static final class QueryPart implements Cloneable {
        String[] mColumns;
        String mTable;
        String mThisJoiningColumn;
        String mOtherJoiningColumn;
        String mOtherJoiningTable;
        boolean mIsLeftJoin;
        String mSelection;
        String[] mGroup;
        String[] mOrder;
        boolean mDistinct;

        public QueryPart(String tableName) {
            mTable = tableName;
        }

        public QueryPart setColumns(String... columns) {
            mColumns = columns;
            return this;
        }

        public QueryPart setThisJoinColumn(String joinColumn) {
            mThisJoiningColumn = joinColumn;
            return this;
        }

        /**
         *
         * @param joinColumn column of other table by which this table will join. Default column is "_id".
         * @return <b>this</b> object
         */
        public QueryPart setOtherJoinColumn(String joinColumn) {
            mOtherJoiningColumn = joinColumn;
            return this;
        }

        /**
         *
         * @param joinTable to table from the param will be joined table from this part. If is not set previous table will be used.
         * @return <b>this</b> object
         */
        public QueryPart setOtherJoinTable(String joinTable) {
            mOtherJoiningTable = joinTable;
            return this;
        }

        /**
         *
         * @param leftJoin <b>true</b> to use LEFT OUTER JOIN and <b>false</b> to use INNER JOIN. By default this option is set to <b>false</b>, there is no need to set it to <b>false</b> explicitly.
         * @return <b>this</b> object
         */
        public QueryPart setLeftJoin(boolean leftJoin) {
            mIsLeftJoin = leftJoin;
            return this;
        }

        public QueryPart setSelection(String selection) {
            mSelection = selection;
            return this;
        }

        public QueryPart setGroup(String... group) {
            mGroup = group;
            return this;
        }

        public QueryPart setOrder(String... order) {
            mOrder = order;
            return this;
        }

        public QueryPart setDistinct(boolean distinct) {
            mDistinct = distinct;
            return this;
        }

        @Override
        public QueryPart clone() {
            QueryPart cloned = null;
            try {
                cloned = (QueryPart) super.clone();
                cloned.mTable = mTable;
                System.arraycopy(mColumns, 0, cloned.mColumns, 0, mColumns.length);
                cloned.mThisJoiningColumn = mThisJoiningColumn;
                cloned.mOtherJoiningColumn = mOtherJoiningColumn;
                cloned.mIsLeftJoin = mIsLeftJoin;
                cloned.mSelection = mSelection;
                System.arraycopy(mGroup, 0, cloned.mGroup, 0, mGroup.length);
                System.arraycopy(mOrder, 0, cloned.mOrder, 0, mOrder.length);
                cloned.mDistinct = mDistinct;
            }
            catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

            return cloned;
        }
    }
}
