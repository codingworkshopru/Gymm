package ru.codingworkshop.gymm.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.databinding.ObservableBoolean;
import android.os.Parcel;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import ru.codingworkshop.gymm.data.model.field.ChildrenField;
import ru.codingworkshop.gymm.data.model.field.Field;
import ru.codingworkshop.gymm.data.GymContract;

/**
 * Created by Радик on 17.02.2017.
 */

public final class ProgramExercise extends Model implements Model.Orderable, Model.Parent<ProgramSet> {
    private ChildrenField<ProgramSet> children;
    public final ObservableBoolean mHasChildren = new ObservableBoolean();

    private static final String TAG = ProgramExercise.class.getSimpleName();

    private static final int ID = 0;
    private static final int EXERCISE = 1;
    private static final int SORT_ORDER = 2;

    private static final String TABLE_NAME = GymContract.ProgramExerciseEntry.TABLE_NAME;

    public ProgramExercise() {
        fields = new Field[3];

        fields[ID] = new Field<Long>(GymContract.ProgramExerciseEntry._ID, 0L);
        fields[EXERCISE] = new Field<Exercise>(GymContract.ProgramExerciseEntry.COLUMN_EXERCISE_ID, Exercise.class);
        fields[SORT_ORDER] = new Field<Integer>(GymContract.ProgramExerciseEntry.COLUMN_SORT_ORDER, -1);

        children = new ChildrenField<>(ProgramSet.class);
    }

    @Override
    public long getId() {
        return (long) fields[ID].getData();
    }

    @Override
    public int getOrder() {
        return (int) fields[SORT_ORDER].getData();
    }

    @Override
    public void setOrder(int order) {
        fields[SORT_ORDER].setData(order);
    }

    public Exercise getExercise() {
        return (Exercise) fields[EXERCISE].getData();
    }

    public void setExercise(Exercise exercise) {
        fields[EXERCISE].setData(exercise);
    }

    @Override
    public void addChild(ProgramSet set) {
        children.add(set);
        mHasChildren.set(childrenCount() != 0);
    }

    @Override
    public void setChild(int index, ProgramSet set) {
        children.set(index, set);
    }

    @Override
    public void moveChild(int fromIndex, int toIndex) {
        children.move(fromIndex, toIndex);
    }

    @Override
    public ProgramSet removeChild(int index) {
        ProgramSet removed = children.remove(index);
        mHasChildren.set(childrenCount() != 0);
        return removed;
    }

    @Override
    public ProgramSet getChild(int index) {
        Log.d(TAG, "Order of instance is " + children.get(index).getOrder());
        return children.get(index);
    }

    @Override
    public int childrenCount() {
        return children.size();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ProgramExercise cloned = (ProgramExercise) super.clone();
        cloned.children = (ChildrenField<ProgramSet>) children.clone();
        return cloned;
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
        cv.put(GymContract.ProgramExerciseEntry.COLUMN_PROGRAM_TRAINING_ID, parentId);
        cv.put(fields[EXERCISE].getColumnName(), ((Exercise) fields[EXERCISE].getData()).getId());
        cv.put(fields[SORT_ORDER].getColumnName(), getOrder());

        long exerciseId = db.insert(TABLE_NAME, null, cv);

        if (exerciseId == -1)
            throw new SQLiteException("Insertion error: " + cv);

        fields[ID].setData(exerciseId);
        commit();

        children.save(db, getId());

        return exerciseId;
    }

    public static List<ProgramExercise> read(SQLiteDatabase db, long parentId) {
        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                GymContract.ProgramExerciseEntry.COLUMN_PROGRAM_TRAINING_ID + "=" + parentId,
                null,
                null,
                null,
                GymContract.ProgramExerciseEntry.COLUMN_SORT_ORDER
        );

        List<ProgramExercise> result = new LinkedList<>();
        while (cursor.moveToNext()) {
            ProgramExercise exercise = new ProgramExercise();
            Field[] fields = exercise.fields;
            fields[ID].setInitialData(cursor.getLong(cursor.getColumnIndex(fields[ID].getColumnName())));
            fields[EXERCISE].setInitialData(Exercise.read(db, cursor.getLong(cursor.getColumnIndex(GymContract.ProgramExerciseEntry.COLUMN_EXERCISE_ID))));
            fields[SORT_ORDER].setInitialData(cursor.getInt(cursor.getColumnIndex(fields[SORT_ORDER].getColumnName())));
            exercise.children.setInitialList(ProgramSet.read(db, exercise.getId()));
            result.add(exercise);
            exercise.mHasChildren.set(exercise.childrenCount() != 0);
        }

        cursor.close();

        return result;
    }

    @Override
    public int update(SQLiteDatabase db) {
        if (getId() == 0)
            return 0;

        ContentValues cv = new ContentValues();
        if (fields[EXERCISE].isChanged())
            cv.put(fields[EXERCISE].getColumnName(), ((Exercise) fields[EXERCISE].getData()).getId());

        if (fields[SORT_ORDER].isChanged())
            cv.put(fields[SORT_ORDER].getColumnName(), (Integer) fields[SORT_ORDER].getData());

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

    private ProgramExercise(Parcel in) {
        super(in);
        children = in.readParcelable(ChildrenField.class.getClassLoader());
        mHasChildren.set(childrenCount() != 0);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(children, flags);
    }

    public static final Creator<ProgramExercise> CREATOR = new Creator<ProgramExercise>() {
        @Override
        public ProgramExercise createFromParcel(Parcel source) {
            return new ProgramExercise(source);
        }

        @Override
        public ProgramExercise[] newArray(int size) {
            return new ProgramExercise[size];
        }
    };
}
