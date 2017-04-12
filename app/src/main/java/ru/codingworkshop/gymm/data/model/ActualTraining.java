package ru.codingworkshop.gymm.data.model;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.codingworkshop.gymm.data.GymContract.ActualTrainingEntry;
import ru.codingworkshop.gymm.data.model.base.Model;
import ru.codingworkshop.gymm.data.model.base.Parent;
import ru.codingworkshop.gymm.data.model.field.ChildrenField;
import ru.codingworkshop.gymm.data.model.field.Field;

/**
 * Created by Радик on 12.04.2017.
 */

public class ActualTraining extends Model implements Parent<ActualExercise> {
    private Field<Long> id = new Field<>(ActualTrainingEntry._ID, 0L);
    private Field<String> startTime = new Field<>(ActualTrainingEntry.COLUMN_START_TIME, String.class);
    private Field<String> finishTime = new Field<>(ActualTrainingEntry.COLUMN_FINISH_TIME, String.class);
    private Field<ProgramTraining> programTraining = new Field<>(ActualTrainingEntry.COLUMN_PROGRAM_TRAINING_ID, ProgramTraining.class);

    private ChildrenField<ActualExercise> children = new ChildrenField<>(ActualExercise.class);

    private static final String TABLE_NAME = ActualTrainingEntry.TABLE_NAME;

    @Override
    public long getId() {
        return id.getData();
    }

    public String getStartTime() {
        return startTime.getData();
    }

    public String getFinishTime() {
        return finishTime.getData();
    }

    public void setFinishTime() {
        SimpleDateFormat f = new SimpleDateFormat("y-M-d H:m:s", Locale.getDefault());
        finishTime.setData(f.format(new Date(System.currentTimeMillis())));
    }

    public ProgramTraining getProgramTraining() {
        return programTraining.getData();
    }

    public void setProgramTraining(ProgramTraining programTraining) {
        this.programTraining.setData(programTraining);
    }

    @Override
    public long create(SQLiteDatabase db, long parentId) {
        return create(db, TABLE_NAME, null, 0, id);
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
        return finishTime.isChanged() || programTraining.isChanged() || children.isChanged();
    }

    @Override
    protected void commit() {
        id.commit();
        finishTime.commit();
        programTraining.commit();
    }

    @Override
    public String validate() {
        return null;
    }

    @Override
    protected void addFieldsToContentValues(ContentValues cv, boolean onlyChanged) {
        Field.addToValues(cv, finishTime, onlyChanged);
        Field.addToValues(cv, programTraining, onlyChanged);
    }

    private ActualTraining(Parcel in) {
        ClassLoader fieldClassLoader = Field.class.getClassLoader();

        id = in.readParcelable(fieldClassLoader);
        startTime = in.readParcelable(fieldClassLoader);
        finishTime = in.readParcelable(fieldClassLoader);
        programTraining = in.readParcelable(fieldClassLoader);
        children = in.readParcelable(ChildrenField.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(id, flags);
        dest.writeParcelable(startTime, flags);
        dest.writeParcelable(finishTime, flags);
        dest.writeParcelable(programTraining, flags);
        dest.writeParcelable(children, flags);
    }

    public static final Creator<ActualTraining> CREATOR = new Creator<ActualTraining>() {
        @Override
        public ActualTraining createFromParcel(Parcel source) {
            return new ActualTraining(source);
        }

        @Override
        public ActualTraining[] newArray(int size) {
            return new ActualTraining[size];
        }
    };

    @Override
    public void addChild(ActualExercise model) {
        children.add(model);
    }

    @Override
    public void setChild(int index, ActualExercise model) {

    }

    @Override
    public void moveChild(int fromIndex, int toIndex) {

    }

    @Override
    public ActualExercise removeChild(int index) {
        return children.remove(index);
    }

    @Override
    public int restoreLastRemoved() {
        return 0;
    }

    @Override
    public ActualExercise getChild(int index) {
        return children.get(index);
    }

    @Override
    public int childrenCount() {
        return children.size();
    }
}
