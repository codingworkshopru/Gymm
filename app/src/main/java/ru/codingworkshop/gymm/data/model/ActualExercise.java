package ru.codingworkshop.gymm.data.model;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;

import ru.codingworkshop.gymm.data.model.base.MutableModel;
import ru.codingworkshop.gymm.data.model.base.Parent;

/**
 * Created by Радик on 12.03.2017.
 */

public class ActualExercise extends MutableModel implements Parent<ActualSet> {
    @Override
    public void addChild(ActualSet model) {

    }

    @Override
    public void setChild(int index, ActualSet model) {

    }

    @Override
    public void moveChild(int fromIndex, int toIndex) {

    }

    @Override
    public ActualSet removeChild(int index) {
        return null;
    }

    @Override
    public int restoreLastRemoved() {
        return 0;
    }

    @Override
    public ActualSet getChild(int index) {
        return null;
    }

    @Override
    public int childrenCount() {
        return 0;
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public long create(SQLiteDatabase db, long parentId) {
        return 0;
    }

    @Override
    public int update(SQLiteDatabase db) {
        return 0;
    }

    @Override
    public int delete(SQLiteDatabase db) {
        return 0;
    }

    @Override
    public boolean isChanged() {
        return false;
    }

    @Override
    protected void commit() {

    }

    @Override
    public String validate() {
        return null;
    }

    @Override
    protected void addFieldsToContentValues(ContentValues cv, boolean onlyChanged) {

    }

    public final Creator<ActualExercise> CREATOR = new Creator<ActualExercise>() {
        @Override
        public ActualExercise createFromParcel(Parcel source) {
            return null;
        }

        @Override
        public ActualExercise[] newArray(int size) {
            return new ActualExercise[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
