package ru.codingworkshop.gymm.data;

import android.provider.BaseColumns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Радик on 21.02.2017.
 */

public final class QueryBuilder {

    public static String build(QueryPart[] parts) {
        String sqlQuery;
        int partsCount = parts.length;

        StringBuilder selection = new StringBuilder();
        StringBuilder from = new StringBuilder();
        StringBuilder group = new StringBuilder();
        StringBuilder order = new StringBuilder();
        for (int i = 0; i < partsCount; i++) {
            QueryPart part = parts[i];
            String table = part.mTable;

            if (!part.mSelection.isEmpty()) {
                if (selection.toString().isEmpty()) {
                    selection.append("SELECT ");
                    if (part.mDistinct)
                        selection.append("DISTINCT ");
                }
                selection.append(part.mSelection).append(",");
            }

            if (i == 0)
                from.append(" FROM ").append(table);
            else {
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

            if (!part.mGroup.isEmpty()) {
                if (group.toString().isEmpty())
                    group.append(" GROUP BY ");
                group.append(part.mGroup).append(",");
            }

            if (!part.mOrder.isEmpty()) {
                if (order.toString().isEmpty())
                    order.append(" ORDER BY ");
                order.append(part.mOrder).append(",");
            }
        }

        deleteLastComma(selection);
        deleteLastComma(group);
        deleteLastComma(order);

        sqlQuery = selection.toString() + from.toString() + group.toString() + order.toString();

        return sqlQuery;
    }

    private static void deleteLastComma(StringBuilder sb) {
        int lastCommaIndex = sb.lastIndexOf(",");
        if (lastCommaIndex != -1)
            sb.deleteCharAt(lastCommaIndex);
    }

    private static String getColumnsWithTable(String[] selection, String table) {
        if (selection == null || selection.length == 0)
            return "";

        StringBuilder selectionWithTable = new StringBuilder();
        for (String s : selection) {
            // if field is a standard SQL function
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

    public static final class QueryPartBuilder {
        private String[] mSelection;
        private String[] mGroup;
        private String[] mOrder;
        private QueryPart mQueryPart;

        public QueryPartBuilder() {
            mQueryPart = new QueryPart();
        }

        public QueryPartBuilder setSelection(String[] selection) {
            mSelection = selection;
            return this;
        }

        public QueryPartBuilder setTable(String table) {
            mQueryPart.mTable = table;
            return this;
        }

        public QueryPartBuilder setThisJoinColumn(String joinColumn) {
            mQueryPart.mThisJoiningColumn = joinColumn;
            return this;
        }

        public QueryPartBuilder setOtherJoinColumn(String joinColumn) {
            mQueryPart.mOtherJoiningColumn = joinColumn;
            return this;
        }

        public QueryPartBuilder setGroup(String[] group) {
            mGroup = group;
            return this;
        }

        public QueryPartBuilder setOrder(String[] order) {
            mOrder = order;
            return this;
        }

        public QueryPartBuilder setDistinct(boolean distinct) {
            mQueryPart.mDistinct = distinct;
            return this;
        }

        public QueryPart build() {
            String table = mQueryPart.mTable;
            mQueryPart.mSelection = getColumnsWithTable(mSelection, table);
            mQueryPart.mGroup = getColumnsWithTable(mGroup, table);
            mQueryPart.mOrder = getColumnsWithTable(mOrder, table);

            return mQueryPart;
        }
    }

    public static final class QueryPart {
        String mSelection;
        String mTable;
        String mThisJoiningColumn;
        String mOtherJoiningColumn;
        String mGroup;
        String mOrder;
        boolean mDistinct;
    }
}
