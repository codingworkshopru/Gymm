package ru.codingworkshop.gymm.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import ru.codingworkshop.gymm.data.GymContract.ProgramTrainingEntry;
import ru.codingworkshop.gymm.data.model.base.Model;
import ru.codingworkshop.gymm.data.model.base.Parent;
import ru.codingworkshop.gymm.data.model.field.ChildrenField;
import ru.codingworkshop.gymm.data.model.field.Field;

/**
 * Created by Радик on 18.02.2017.
 */

public final class ProgramTraining extends Model implements Parent<ProgramExercise> {
    private Field<Long> id = new Field<>(ProgramTrainingEntry._ID, 0L);
    private Field<String> name = new Field<>(ProgramTrainingEntry.COLUMN_NAME, String.class);
    private Field<Integer> weekday = new Field<>(ProgramTrainingEntry.COLUMN_WEEKDAY, 0);
    private ChildrenField<ProgramExercise> children = new ChildrenField<>(ProgramExercise.class);

    private static final int FIRST_DAY_OF_WEEK = Calendar.getInstance().getFirstDayOfWeek();

    private static final String TABLE_NAME = ProgramTrainingEntry.TABLE_NAME;

    private static final String TAG = ProgramTraining.class.getSimpleName();

    public ProgramTraining() {
    }

    @Override
    public long getId() {
        return id.getData();
    }

    public String getName() {
        return name.getData();
    }

    public void setName(String name) {
        this.name.setData(name);
    }

    public int getWeekday() {
        // cast weekday accordingly to first day of week
        int weekday = this.weekday.getData();
        if (weekday != 0) {
            weekday -= FIRST_DAY_OF_WEEK;
            if (weekday >= 0)
                weekday++;
            else
                weekday += 8;
        }
        return weekday;
    }

    public void setWeekday(int weekday) {
        // cast weekday accordingly to first day of week
        if (weekday != 0) {
            weekday += FIRST_DAY_OF_WEEK;
            if (weekday <= 8)
                weekday--;
            else
                weekday -= 8;
        }
        this.weekday.setData(weekday);
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
    public int restoreLastRemoved() {
        return children.restoreLastRemoved();
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
        return name.isChanged() || weekday.isChanged() || children.isChanged();
    }

    @Override
    public String validate() {
        return null;
    }

    @Override
    protected void commit() {
        id.commit();
        name.commit();
        weekday.commit();
    }

    @Override
    protected void addFieldsToContentValues(ContentValues cv, boolean onlyChanged) {
        Field.addToValues(cv, name, onlyChanged);
        Field.addToValues(cv, weekday, onlyChanged);
    }

    @Override
    public long create(SQLiteDatabase db, long parentId) {
        long trainingId = create(db, TABLE_NAME, null, 0, id);
        children.save(db, trainingId);
        return trainingId;
    }

    public static ProgramTraining load(SQLiteDatabase db, long id) {
        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                ProgramTrainingEntry._ID + "= ?",
                new String[] {Long.toString(id)},
                null,
                null,
                null
        );

        ProgramTraining result = new ProgramTraining();
        if (cursor.moveToNext()) {
            result.id.setInitialData(cursor.getLong(cursor.getColumnIndex(result.id.getColumnName())));
            result.name.setInitialData(cursor.getString(cursor.getColumnIndex(result.name.getColumnName())));
            result.weekday.setInitialData(cursor.getInt(cursor.getColumnIndex(result.weekday.getColumnName())));
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
            training.id.setInitialData(cursor.getLong(cursor.getColumnIndex(training.id.getColumnName())));
            training.name.setInitialData(cursor.getString(cursor.getColumnIndex(training.name.getColumnName())));
            training.weekday.setInitialData(cursor.getInt(cursor.getColumnIndex(training.weekday.getColumnName())));
            training.children.setInitialList(ProgramExercise.read(db, training.getId()));
            result.add(training);
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

    private ProgramTraining(Parcel in) {
        id = in.readParcelable(Field.class.getClassLoader());
        name = in.readParcelable(Field.class.getClassLoader());
        weekday = in.readParcelable(Field.class.getClassLoader());
        children = in.readParcelable(ChildrenField.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(id, flags);
        dest.writeParcelable(name, flags);
        dest.writeParcelable(weekday, flags);
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
