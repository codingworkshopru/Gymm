package ru.codingworkshop.gymm.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Parcel;

import java.util.LinkedList;
import java.util.List;

import ru.codingworkshop.gymm.data.model.field.Field;
import ru.codingworkshop.gymm.data.GymContract;

/**
 * Created by Радик on 17.02.2017.
 */

public final class ProgramSet extends Model implements Model.Orderable {
    private static final int ID  = 0;
    private static final int REPS = 1;
    private static final int SECONDS_FOR_REST = 2;
    private static final int SORT_ORDER = 3;

    private static final String TABLE_NAME = GymContract.ProgramSetEntry.TABLE_NAME;

    public ProgramSet() {
        fields = new Field[4];
        fields[ID] = new Field<Long>(GymContract.ProgramSetEntry._ID, 0L);
        fields[REPS] = new Field<Integer>(GymContract.ProgramSetEntry.COLUMN_REPS, 0);
        fields[SECONDS_FOR_REST] = new Field<Integer>(GymContract.ProgramSetEntry.COLUMN_SECONDS_FOR_REST, 0);
        fields[SORT_ORDER] = new Field<Integer>(GymContract.ProgramSetEntry.COLUMN_SORT_ORDER, -1);
    }

    @Override
    public long getId() {
        return (long) fields[ID].getData();
    }

    public void setReps(int reps) {
        fields[REPS].setData(reps);
    }

    public int getReps() {
        return (int) fields[REPS].getData();
    }

    public void setSecondsForRest(int secondsForRest) {
        fields[SECONDS_FOR_REST].setData(secondsForRest);
    }

    public int getSecondsForRest() {
        return (int) fields[SECONDS_FOR_REST].getData();
    }

    @Override
    public void setOrder(int order) {
        fields[SORT_ORDER].setData(order);
    }

    @Override
    public int getOrder() {
        return (int) fields[SORT_ORDER].getData();
    }

    @Override
    public ProgramSet clone() {
        try {
            return (ProgramSet) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isChanged() {
        for (Field f : fields)
            if (f.isChanged())
                return true;

        return false;
    }

    @Override
    public long create(SQLiteDatabase db, long parentId) {
        if (getId() != 0)
            return -1;

        ContentValues cv = new ContentValues();
        cv.put(GymContract.ProgramSetEntry.COLUMN_PROGRAM_EXERCISE_ID, parentId);

        for (int i = ID + 1; i <= SORT_ORDER; i++)
            cv.put(fields[i].getColumnName(), (Integer) fields[i].getData());

        long setId = db.insert(TABLE_NAME, null, cv);

        if (setId == -1)
            throw new SQLiteException("Insertion error: " + cv);

        fields[ID].setData(setId);
        commit();

        return setId;
    }

    public static List<ProgramSet> read(SQLiteDatabase db, long parentId) {
        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                GymContract.ProgramSetEntry.COLUMN_PROGRAM_EXERCISE_ID + "=" + parentId,
                null,
                null,
                null,
                GymContract.ProgramSetEntry.COLUMN_SORT_ORDER
        );

        List<ProgramSet> result = new LinkedList<>();
        while (cursor.moveToNext()) {
            ProgramSet set = new ProgramSet();
            Field[] fields = set.fields;
            fields[ID].setInitialData(cursor.getLong(cursor.getColumnIndex(fields[ID].getColumnName())));
            fields[REPS].setInitialData(cursor.getInt(cursor.getColumnIndex(fields[REPS].getColumnName())));
            fields[SECONDS_FOR_REST].setInitialData(cursor.getInt(cursor.getColumnIndex(fields[SECONDS_FOR_REST].getColumnName())));
            fields[SORT_ORDER].setInitialData(cursor.getInt(cursor.getColumnIndex(fields[SORT_ORDER].getColumnName())));
            result.add(set);
        }

        cursor.close();

        return result;
    }

    @Override
    public int update(SQLiteDatabase db) {
        if (getId() == 0)
            return 0;

        ContentValues cv = new ContentValues();

        for (Field f : fields)
            if (f.isChanged())
                cv.put(f.getColumnName(), (Integer) f.getData());

        if (cv.size() == 0)
            return 0;

        commit();

        return db.update(TABLE_NAME, cv, fields[ID].getColumnName() + "=" + getId(), null);
    }

    @Override
    public int delete(SQLiteDatabase db) {
        int rows = db.delete(TABLE_NAME, fields[ID].getColumnName() + "=" + getId(), null);
        if (rows == 1) {
            fields[ID].setData(0L);
            fields[ID].commit();
        }
        return rows;
    }

    private ProgramSet(Parcel in) {
        super(in);
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
}
