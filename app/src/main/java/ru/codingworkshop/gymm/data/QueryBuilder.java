package ru.codingworkshop.gymm.data;

import android.provider.BaseColumns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Радик on 21.02.2017.
 */

public final class QueryBuilder {
    private static final String TAG = QueryBuilder.class.getSimpleName();

    public static String build(QueryPart... parts) {
        int partsCount = parts.length;

        StringBuilder columns = new StringBuilder("SELECT ");
        StringBuilder from = new StringBuilder(" FROM ");
        StringBuilder where = new StringBuilder();
        StringBuilder group = new StringBuilder();
        StringBuilder order = new StringBuilder();
        for (int i = 0; i < partsCount; i++) {
            QueryPart part = parts[i];
            String table = part.mTable;

            if (part.mColumns != null && !part.mColumns.isEmpty()) {
                if (i == 0 && part.mDistinct)
                    columns.append("DISTINCT ");
                columns.append(part.mColumns).append(",");
            }

            if (i == 0) {
                from.append(table);
            } else {
                if (part.mThisJoiningColumn == null)
                    throw new IllegalArgumentException("joining column not presented");
                else
                    part.mThisJoiningColumn = getColumnWithTable(part.mThisJoiningColumn, table);

                String otherColumn = part.mOtherJoiningColumn == null ? BaseColumns._ID : part.mOtherJoiningColumn;
                part.mOtherJoiningColumn = getColumnWithTable(otherColumn, parts[i-1].mTable);

                from.append(" JOIN ")
                        .append(table)
                        .append(" ON ")
                        .append(part.mThisJoiningColumn)
                        .append("=")
                        .append(part.mOtherJoiningColumn);
            }

            if (part.mSelection != null && !part.mSelection.isEmpty()) {
                if (where.toString().isEmpty())
                    where.append(" WHERE ");
                where.append(part.mSelection).append(" AND ");
            }

            if (part.mGroup != null && !part.mGroup.isEmpty()) {
                if (group.toString().isEmpty())
                    group.append(" GROUP BY ");
                group.append(part.mGroup).append(",");
            }

            if (part.mOrder != null && !part.mOrder.isEmpty()) {
                if (order.toString().isEmpty())
                    order.append(" ORDER BY ");
                order.append(part.mOrder).append(",");
            }
        }

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

    private static String getColumnsWithTable(String[] selection, String table) {
        if (selection == null || selection.length == 0)
            return "";

        StringBuilder selectionWithTable = new StringBuilder();
        for (String s : selection) {
            // if a standard SQL function presented
            if (s.matches("^\\w+\\(\\w*\\s?\\w+\\)$")) {
                Pattern pattern = Pattern.compile("(\\w+)\\)$");
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    String column = getColumnWithTable(matcher.group(), table);
                    System.out.println(column);
                    selectionWithTable.append(matcher.replaceFirst(column));
                }
            } else {
                selectionWithTable.append(getColumnWithTable(s, table));
            }
            selectionWithTable.append(",");
        }
        deleteLastComma(selectionWithTable);
        return selectionWithTable.toString();
    }

    private static String getColumnWithTable(String column, String table) {
        return table + "." + column;
    }

    public static final class QueryPart {
        String mColumns;
        String mTable;
        String mThisJoiningColumn;
        String mOtherJoiningColumn;
        String mSelection;
        String mGroup;
        String mOrder;
        boolean mDistinct;

        public QueryPart(String tableName) {
            mTable = tableName;
        }

        public QueryPart setColumns(String... columns) {
            mColumns = getColumnsWithTable(columns, mTable);
            return this;
        }

        public QueryPart setThisJoinColumn(String joinColumn) {
            mThisJoiningColumn = joinColumn;
            return this;
        }

        public QueryPart setOtherJoinColumn(String joinColumn) {
            mOtherJoiningColumn = joinColumn;
            return this;
        }

        public QueryPart setSelection(String selection) {
            mSelection = selection;
            return this;
        }

        public QueryPart setGroup(String... group) {
            mGroup = getColumnsWithTable(group, mTable);
            return this;
        }

        public QueryPart setOrder(String... order) {
            mOrder = getColumnsWithTable(order, mTable);
            return this;
        }

        public QueryPart setDistinct(boolean distinct) {
            mDistinct = distinct;
            return this;
        }
    }
}
