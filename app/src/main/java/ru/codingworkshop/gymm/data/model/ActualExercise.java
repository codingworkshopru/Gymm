package ru.codingworkshop.gymm.data.model;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;

import ru.codingworkshop.gymm.data.GymContract.ActualExerciseEntry;
import ru.codingworkshop.gymm.data.model.base.Model;
import ru.codingworkshop.gymm.data.model.base.Parent;
import ru.codingworkshop.gymm.data.model.field.ChildrenField;
import ru.codingworkshop.gymm.data.model.field.Field;

/**
 * Created by Радик on 12.03.2017.
 */

public final class ActualExercise extends Model implements Parent<ActualSet> {
    private Field<Long> id = new Field<>(ActualExerciseEntry._ID, 0L);
    private Field<Exercise> exercise = new Field<>(ActualExerciseEntry.COLUMN_EXERCISE_ID, Exercise.class);

    private ChildrenField<ActualSet> children = new ChildrenField<>(ActualSet.class);

    private static final String TABLE_NAME = ActualExerciseEntry.TABLE_NAME;

    @Override
    public long getId() {
        return id.getData();
    }

    public Exercise getExercise() {
        return exercise.getData();
    }

    public void setExercise(Exercise exercise) {
        this.exercise.setData(exercise);
    }

    @Override
    public void addChild(ActualSet model) {
        children.add(model);
    }

    @Override
    public void setChild(int index, ActualSet model) {
        children.set(index, model);
    }

    @Override
    public void moveChild(int fromIndex, int toIndex) {
        children.move(fromIndex, toIndex);
    }

    @Override
    public ActualSet removeChild(int index) {
        return children.remove(index);
    }

    @Override
    public int restoreLastRemoved() {
        return children.restoreLastRemoved();
    }

    @Override
    public ActualSet getChild(int index) {
        return children.get(index);
    }

    @Override
    public int childrenCount() {
        return children.size();
    }

    @Override
    public void saveChildren(SQLiteDatabase db, long parentId) {
        children.save(db, parentId);
    }

    @Override
    public long create(SQLiteDatabase db, long parentId) {
        long exerciseId = create(db, TABLE_NAME, ActualExerciseEntry.COLUMN_EXERCISE_ID, parentId, id);
        saveChildren(db, exerciseId);
        return exerciseId;
    }

    @Override
    public int update(SQLiteDatabase db) {
        saveChildren(db, getId());
        return update(db, TABLE_NAME, id);
    }

    @Override
    public int delete(SQLiteDatabase db) {
        return delete(db, TABLE_NAME, id);
    }

    @Override
    public boolean isChanged() {
        return exercise.isChanged() || children.isChanged();
    }

    @Override
    protected void commit() {
        id.commit();
        exercise.commit();
    }

    @Override
    public String validate() {
        return null;
    }

    @Override
    protected void addFieldsToContentValues(ContentValues cv, boolean onlyChanged) {
        Field.addToValues(cv, id, onlyChanged);
        Field.addToValues(cv, exercise, onlyChanged);
    }

    private ActualExercise(Parcel in) {
        ClassLoader cl = Field.class.getClassLoader();
        id = in.readParcelable(cl);
        exercise = in.readParcelable(cl);
        children = in.readParcelable(ChildrenField.class.getClassLoader());
    }

    public final Creator<ActualExercise> CREATOR = new Creator<ActualExercise>() {
        @Override
        public ActualExercise createFromParcel(Parcel source) {
            return new ActualExercise(source);
        }

        @Override
        public ActualExercise[] newArray(int size) {
            return new ActualExercise[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(id, flags);
        dest.writeParcelable(exercise, flags);
        dest.writeParcelable(children, flags);
    }
}
