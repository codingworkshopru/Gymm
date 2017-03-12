package ru.codingworkshop.gymm.data.model;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;

import ru.codingworkshop.gymm.data.GymContract.ActualExerciseEntry;
import ru.codingworkshop.gymm.data.model.base.MutableModel;
import ru.codingworkshop.gymm.data.model.base.Parent;
import ru.codingworkshop.gymm.data.model.field.ChildrenField;
import ru.codingworkshop.gymm.data.model.field.Field;

/**
 * Created by Радик on 12.03.2017.
 */

public class ActualExercise extends MutableModel implements Parent<ActualSet> {
    private Field<Long> id = new Field<>(ActualExerciseEntry._ID, 0L);
    private Field<Long> trainingTimestamp = new Field<>(ActualExerciseEntry.COLUMN_TRAINING_TIMESTAMP, 0L);
    private ChildrenField<ActualSet> children = new ChildrenField<>(ActualSet.class);

    private static final String TABLE_NAME = ActualExerciseEntry.TABLE_NAME;

    @Override
    public long getId() {
        return id.getData();
    }

    public long getTrainingTimestamp() {
        return trainingTimestamp.getData();
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
    public long create(SQLiteDatabase db, long parentId) {
        return create(db, TABLE_NAME, ActualExerciseEntry.COLUMN_EXERCISE_ID, parentId, id);
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
    public boolean isChanged() {
        return id.isChanged() || trainingTimestamp.isChanged();
    }

    @Override
    protected void commit() {
        id.commit();
        trainingTimestamp.commit();
    }

    @Override
    public String validate() {
        return null;
    }

    @Override
    protected void addFieldsToContentValues(ContentValues cv, boolean onlyChanged) {
        Field.addToValues(cv, id, onlyChanged);
        Field.addToValues(cv, trainingTimestamp, onlyChanged);
    }

    private ActualExercise(Parcel in) {
        ClassLoader cl = Field.class.getClassLoader();
        id = in.readParcelable(cl);
        trainingTimestamp = in.readParcelable(cl);
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
        dest.writeParcelable(trainingTimestamp, flags);
    }
}
