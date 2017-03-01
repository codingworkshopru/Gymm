package ru.codingworkshop.gymm.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Parcel;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import ru.codingworkshop.gymm.data.model.field.ChildrenField;
import ru.codingworkshop.gymm.data.model.field.Field;
import ru.codingworkshop.gymm.data.GymContract;

/**
 * Created by Радик on 18.02.2017.
 */

public final class ProgramTraining extends Model implements Model.Parent<ProgramExercise> {
    private ChildrenField<ProgramExercise> children;

    private static final int ID = 0;
    private static final int NAME = 1;
    private static final int WEEKDAY = 2;

    private static final String TABLE_NAME = GymContract.ProgramTrainingEntry.TABLE_NAME;

    private static final String TAG = ProgramTraining.class.getSimpleName();

    public ProgramTraining() {
        fields = new Field[3];

        fields[ID] = new Field<Long>(GymContract.ProgramTrainingEntry._ID, 0L);
        fields[NAME] = new Field<String>(GymContract.ProgramTrainingEntry.COLUMN_NAME, String.class);
        fields[WEEKDAY] = new Field<Integer>(GymContract.ProgramTrainingEntry.COLUMN_WEEKDAY, 0);

        children = new ChildrenField<>(ProgramExercise.class);

        Log.d(TAG, "ProgramTraining object constructed");
    }

    @Override
    public long getId() {
        return (long) fields[ID].getData();
    }

    public String getName() {
        return (String) fields[NAME].getData();
    }

    public void setName(String name) {
        fields[NAME].setData(name);
    }

    public int getWeekday() {
        return (int) fields[WEEKDAY].getData();
    }

    public void setWeekday(int weekday) {
        fields[WEEKDAY].setData(weekday);
    }

    @Override
    public void addChild(ProgramExercise exercise) {
        children.add(exercise);
    }

    @Override
    public void setChild(int index, ProgramExercise exercise) {
        children.set(index, exercise);
    }

    @Override
    public ProgramExercise removeChild(int index) {
        return children.remove(index);
    }

    @Override
    public void moveChild(int fromIndex, int toIndex) {
        children.move(fromIndex, toIndex);
    }

    @Override
    public ProgramExercise getChild(int index) {
        return children.get(index);
    }

    @Override
    public int childrenCount() {
        return children.size();
    }

    @Override
    public boolean isChanged() {
        for (Field f : fields)
            if (f.isChanged())
                return true;

        return children.isChanged();
    }

    @Override
    public long create(SQLiteDatabase db, long parentId) {
        if (getId() != 0)
            return -1;

        ContentValues cv = new ContentValues();
        cv.put(fields[NAME].getColumnName(), (String) fields[NAME].getData());
        cv.put(fields[WEEKDAY].getColumnName(), (int) fields[WEEKDAY].getData());

        long trainingId = db.insert(TABLE_NAME, null, cv);

        if (trainingId == -1)
            throw new SQLiteException("Insertion error: " + cv);

        fields[ID].setData(trainingId);
        commit();

        children.save(db, getId());

        return trainingId;
    }

    public static ProgramTraining load(SQLiteDatabase db, long id) {
        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                GymContract.ProgramTrainingEntry._ID + "= ?",
                new String[] {Long.toString(id)},
                null,
                null,
                null
        );

        ProgramTraining result = new ProgramTraining();
        if (cursor.moveToNext()) {
            Field[] fields = result.fields;
            fields[ID].setInitialData(cursor.getLong(cursor.getColumnIndex(fields[ID].getColumnName())));
            fields[NAME].setInitialData(cursor.getString(cursor.getColumnIndex(fields[NAME].getColumnName())));
            fields[WEEKDAY].setInitialData(cursor.getInt(cursor.getColumnIndex(fields[WEEKDAY].getColumnName())));
            result.children.setInitialList(ProgramExercise.read(db, result.getId()));
        }

        cursor.close();

        return result;
    }

    public static List<ProgramTraining> read(SQLiteDatabase db, long parentId) {
        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        List<ProgramTraining> result = new LinkedList<>();
        while (cursor.moveToNext()) {
            ProgramTraining training = new ProgramTraining();
            Field[] fields = training.fields;
            fields[ID].setInitialData(cursor.getLong(cursor.getColumnIndex(fields[ID].getColumnName())));
            fields[NAME].setInitialData(cursor.getString(cursor.getColumnIndex(fields[NAME].getColumnName())));
            fields[WEEKDAY].setInitialData(cursor.getInt(cursor.getColumnIndex(fields[WEEKDAY].getColumnName())));
            training.children.setInitialList(ProgramExercise.read(db, training.getId()));
            result.add(training);
        }

        cursor.close();

        return result;
    }

    @Override
    public int update(SQLiteDatabase db) {
        if (getId() == 0)
            return 0;

        ContentValues cv = new ContentValues();
        if (fields[NAME].isChanged())
            cv.put(fields[NAME].getColumnName(), (String) fields[NAME].getData());

        if (fields[WEEKDAY].isChanged())
            cv.put(fields[WEEKDAY].getColumnName(), (int) fields[WEEKDAY].getData());

        children.save(db, getId());

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
            commit();
        }

        return rows;
    }

    private ProgramTraining(Parcel in) {
        super(in);
        children = in.readParcelable(ChildrenField.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(children, flags);
    }

    public static final Creator<ProgramTraining> CREATOR = new Creator<ProgramTraining>() {
        @Override
        public ProgramTraining createFromParcel(Parcel source) {
            return new ProgramTraining(source);
        }

        @Override
        public ProgramTraining[] newArray(int size) {
            return new ProgramTraining[size];
        }
    };
}
