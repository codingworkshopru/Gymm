package ru.codingworkshop.gymm.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;

import java.util.LinkedList;
import java.util.List;

import ru.codingworkshop.gymm.data.GymContract.ProgramExerciseEntry;
import ru.codingworkshop.gymm.data.model.base.MutableModel;
import ru.codingworkshop.gymm.data.model.base.Orderable;
import ru.codingworkshop.gymm.data.model.base.Parent;
import ru.codingworkshop.gymm.data.model.field.ChildrenField;
import ru.codingworkshop.gymm.data.model.field.Field;

/**
 * Created by Радик on 17.02.2017.
 */

public final class ProgramExercise extends MutableModel implements Orderable, Parent<ProgramSet> {
    private Field<Long> id = new Field<>(ProgramExerciseEntry._ID, 0L);
    private Field<Exercise> exercise = new Field<>(ProgramExerciseEntry.COLUMN_EXERCISE_ID, Exercise.class);
    private Field<Integer> sortOrder = new Field<>(ProgramExerciseEntry.COLUMN_SORT_ORDER, -1);
    private ChildrenField<ProgramSet> children = new ChildrenField<>(ProgramSet.class);

    private static final String TAG = ProgramExercise.class.getSimpleName();

    private static final String TABLE_NAME = ProgramExerciseEntry.TABLE_NAME;

    public ProgramExercise() {
    }

    @Override
    public long getId() {
        return id.getData();
    }

    @Override
    public int getOrder() {
        return sortOrder.getData();
    }

    @Override
    public void setOrder(int order) {
        sortOrder.setData(order);
    }

    public Exercise getExercise() {
        return exercise.getData();
    }

    public void setExercise(Exercise exercise) {
        this.exercise.setData(exercise);
    }

    @Override
    public void addChild(ProgramSet set) {
        children.add(set);
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
        return children.remove(index);
    }

    @Override
    public int restoreLastRemoved() {
        return children.restoreLastRemoved();
    }

    @Override
    public ProgramSet getChild(int index) {
        return children.get(index);
    }

    @Override
    public int childrenCount() {
        return children.size();
    }

    @Override
    public ProgramExercise clone() throws CloneNotSupportedException {
        try {
            ProgramExercise cloned = (ProgramExercise) super.clone();
            cloned.id = id.clone();
            cloned.exercise = exercise.clone();
            cloned.sortOrder = sortOrder.clone();
            cloned.children = children.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isChanged() {
        return exercise.isChanged() || sortOrder.isChanged() || children.isChanged();
    }

    @Override
    public String validate() {
        return null;
    }

    @Override
    protected void commit() {
        id.commit();
        exercise.commit();
        sortOrder.commit();
    }

    @Override
    protected void addFieldsToContentValues(ContentValues cv, boolean onlyChanged) {
        Field.addToValues(cv, exercise, onlyChanged);
        Field.addToValues(cv, sortOrder, onlyChanged);
    }

    @Override
    public long create(SQLiteDatabase db, long parentId) {
        long exerciseId = create(db, TABLE_NAME, ProgramExerciseEntry.COLUMN_PROGRAM_TRAINING_ID, parentId, id);

        children.save(db, exerciseId);

        return exerciseId;
    }

    public static List<ProgramExercise> read(SQLiteDatabase db, long parentId) {
        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                ProgramExerciseEntry.COLUMN_PROGRAM_TRAINING_ID + "=" + parentId,
                null,
                null,
                null,
                ProgramExerciseEntry.COLUMN_SORT_ORDER
        );

        List<ProgramExercise> result = new LinkedList<>();
        while (cursor.moveToNext()) {
            ProgramExercise exercise = new ProgramExercise();
            exercise.id.setInitialData(cursor.getLong(cursor.getColumnIndex(exercise.id.getColumnName())));
            exercise.exercise.setInitialData(Exercise.readOne(db, cursor.getLong(cursor.getColumnIndex(ProgramExerciseEntry.COLUMN_EXERCISE_ID))));
            exercise.sortOrder.setInitialData(cursor.getInt(cursor.getColumnIndex(exercise.sortOrder.getColumnName())));
            exercise.children.setInitialList(ProgramSet.read(db, exercise.getId()));
            result.add(exercise);
        }

        cursor.close();

        return result;
    }

    @Override
    public int update(SQLiteDatabase db) {
        children.save(db, getId());

        return update(db, TABLE_NAME, id);
    }

    @Override
    public int delete(SQLiteDatabase db) {
        return delete(db, TABLE_NAME, id);
    }

    private ProgramExercise(Parcel in) {
        id = in.readParcelable(Field.class.getClassLoader());
        exercise = in.readParcelable(Field.class.getClassLoader());
        sortOrder = in.readParcelable(Field.class.getClassLoader());
        children = in.readParcelable(ChildrenField.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(id, flags);
        dest.writeParcelable(exercise, flags);
        dest.writeParcelable(sortOrder, flags);
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
