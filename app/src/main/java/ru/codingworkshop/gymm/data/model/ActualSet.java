package ru.codingworkshop.gymm.data.model;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;

import ru.codingworkshop.gymm.data.GymContract.ActualSetEntry;
import ru.codingworkshop.gymm.data.model.base.MutableModel;
import ru.codingworkshop.gymm.data.model.field.Field;

/**
 * Created by Радик on 12.03.2017.
 */

public class ActualSet extends MutableModel  {
    private Field<Long> id = new Field<>(ActualSetEntry._ID, 0L);
    private Field<Integer> reps = new Field<>(ActualSetEntry.COLUMN_REPS, 0);
    private Field<Double> weight = new Field<>(ActualSetEntry.COLUMN_WEIGHT, 0d);

    private static final String TABLE_NAME = ActualSetEntry.TABLE_NAME;

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

    public void setWeight(double weight) {
        this.weight.setData(weight);
    }

    public double getWeight() {
        return weight.getData();
    }

    @Override
    public long create(SQLiteDatabase db, long parentId) {
        return create(db, TABLE_NAME, ActualSetEntry.COLUMN_ACTUAL_EXERCISE_ID, parentId, id);
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
        return reps.isChanged() || weight.isChanged();
    }

    @Override
    public String validate() {
        return null;
    }

    @Override
    protected void commit() {
        id.commit();
        reps.commit();
        weight.commit();
    }

    @Override
    protected void addFieldsToContentValues(ContentValues cv, boolean onlyChanged) {
        Field.addToValues(cv, reps, onlyChanged);
        Field.addToValues(cv, weight, onlyChanged);
    }

    private ActualSet(Parcel in) {
        ClassLoader cl = Field.class.getClassLoader();
        id = in.readParcelable(cl);
        reps = in.readParcelable(cl);
        weight = in.readParcelable(cl);
    }

    public static final Creator<ActualSet> CREATOR = new Creator<ActualSet>() {
        @Override
        public ActualSet createFromParcel(Parcel source) {
            return new ActualSet(source);
        }

        @Override
        public ActualSet[] newArray(int size) {
            return new ActualSet[size];
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
        dest.writeParcelable(weight, flags);
    }
}