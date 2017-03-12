package ru.codingworkshop.gymm.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;

import java.util.LinkedList;
import java.util.List;

import ru.codingworkshop.gymm.data.GymContract.ProgramSetEntry;
import ru.codingworkshop.gymm.data.QueryBuilder;
import ru.codingworkshop.gymm.data.model.base.MutableModel;
import ru.codingworkshop.gymm.data.model.base.Orderable;
import ru.codingworkshop.gymm.data.model.field.Field;

/**
 * Created by Радик on 17.02.2017.
 */

public final class ProgramSet extends MutableModel implements Orderable {
    private Field<Long> id = new Field<>(ProgramSetEntry._ID, 0L);
    private Field<Integer> reps = new Field<>(ProgramSetEntry.COLUMN_REPS, 0);
    private Field<Integer> secondsForRest = new Field<>(ProgramSetEntry.COLUMN_SECONDS_FOR_REST, 0);
    private Field<Integer> sortOrder = new Field<>(ProgramSetEntry.COLUMN_SORT_ORDER, -1);

    private static final String TABLE_NAME = ProgramSetEntry.TABLE_NAME;

    public ProgramSet() {
    }

    @Override
    public long getId() {
        return id.getData();
    }

    public void setReps(int reps) {
        this.reps.setData(reps);
    }

    public int getReps() {
        return reps.getData();
    }

    private void setSecondsForRest(int secondsForRest) {
        this.secondsForRest.setData(secondsForRest);
    }

    private int getSecondsForRest() {
        return secondsForRest.getData();
    }

    public void setTimeForRest(int minutes, int seconds) {
        setSecondsForRest(minutes * 60 + seconds);
    }

    public int getRestMinutes() {
        return getSecondsForRest() / 60;
    }

    public int getRestSeconds() {
        return getSecondsForRest() % 60;
    }

    @Override
    public void setOrder(int order) {
        sortOrder.setData(order);
    }

    @Override
    public int getOrder() {
        return sortOrder.getData();
    }

    @Override
    public ProgramSet clone() {
        try {
            ProgramSet cloned = (ProgramSet) super.clone();
            cloned.id = id.clone();
            cloned.reps = reps.clone();
            cloned.secondsForRest = secondsForRest.clone();
            cloned.sortOrder = sortOrder.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isChanged() {
        return reps.isChanged() || secondsForRest.isChanged() || sortOrder.isChanged();
    }

    protected void addFieldsToContentValues(ContentValues cv, boolean onlyChanged) {
        Field.addToValues(cv, reps, onlyChanged);
        Field.addToValues(cv, secondsForRest, onlyChanged);
        Field.addToValues(cv, sortOrder, onlyChanged);
    }

    @Override
    protected void commit() {
        id.commit();
        reps.commit();
        secondsForRest.commit();
        sortOrder.commit();
    }

    @Override
    public long create(SQLiteDatabase db, long parentId) {
        return create(db, TABLE_NAME, ProgramSetEntry.COLUMN_PROGRAM_EXERCISE_ID, parentId, id);
    }

    public static List<ProgramSet> read(SQLiteDatabase db, long parentId) {
        QueryBuilder.QueryPart part = new QueryBuilder.QueryPart(TABLE_NAME);
        part.setSelection(ProgramSetEntry.COLUMN_PROGRAM_EXERCISE_ID + "=?")
                .setOrder(ProgramSetEntry.COLUMN_SORT_ORDER);

        Cursor cursor = db.rawQuery(QueryBuilder.build(part), new String[] {String.valueOf(parentId)});

        List<ProgramSet> result = new LinkedList<>();
        while (cursor.moveToNext()) {
            ProgramSet set = new ProgramSet();
            set.id.setInitialData(cursor.getLong(cursor.getColumnIndex(set.id.getColumnName())));

            set.reps.setInitialData(cursor.getInt(cursor.getColumnIndex(set.reps.getColumnName())));
            set.secondsForRest.setInitialData(cursor.getInt(cursor.getColumnIndex(set.secondsForRest.getColumnName())));
            set.sortOrder.setInitialData(cursor.getInt(cursor.getColumnIndex(set.sortOrder.getColumnName())));
            result.add(set);
        }

        cursor.close();

        return result;
    }

    @Override
    public int update(SQLiteDatabase db) {
        return update(db, TABLE_NAME, id);
    }

    @Override
    public int delete(SQLiteDatabase db) {
        return delete(db, TABLE_NAME, id);
    }

    @Override
    public String validate() {
        return null;
    }

    private ProgramSet(Parcel in) {
        ClassLoader cl = Field.class.getClassLoader();
        id = in.readParcelable(cl);
        reps = in.readParcelable(cl);
        secondsForRest = in.readParcelable(cl);
        sortOrder = in.readParcelable(cl);
    }

    public static final Creator<ProgramSet> CREATOR = new Creator<ProgramSet>() {
        @Override
        public ProgramSet createFromParcel(Parcel in) {
            return new ProgramSet(in);
        }

        @Override
        public ProgramSet[] newArray(int size) {
            return new ProgramSet[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(id, flags);
        dest.writeParcelable(reps, flags);
        dest.writeParcelable(secondsForRest, flags);
        dest.writeParcelable(sortOrder, flags);
    }
}
