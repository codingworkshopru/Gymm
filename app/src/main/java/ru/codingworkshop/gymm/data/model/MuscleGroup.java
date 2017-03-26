package ru.codingworkshop.gymm.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

import ru.codingworkshop.gymm.data.GymContract.MuscleGroupEntry;
import ru.codingworkshop.gymm.data.QueryBuilder;
import ru.codingworkshop.gymm.data.model.base.MutableModel;

/**
 * Created by Радик on 14.02.2017.
 */

public final class MuscleGroup extends MutableModel {
    private long id;
    private String name;
    private String mapColorRgb;
    private boolean isAnterior;

    private static final String TABLE_NAME = MuscleGroupEntry.TABLE_NAME;

    public MuscleGroup() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMapColorRgb() {
        return mapColorRgb;
    }

    public void setMapColorRgb(String mapColorRgb) {
        this.mapColorRgb = mapColorRgb;
    }

    public boolean isAnterior() {
        return isAnterior;
    }

    public void setAnterior(boolean isAnterior) {
        this.isAnterior = isAnterior;
    }

    @Override
    public long create(SQLiteDatabase db, long parentId) {
        id = super.create(db, TABLE_NAME, null, 0, null);
        return id;
    }

    public static List<MuscleGroup> read(SQLiteDatabase db) {
        List<MuscleGroup> result = new ArrayList<>();

        QueryBuilder.QueryPart part = new QueryBuilder.QueryPart(TABLE_NAME);

        Cursor cursor = db.rawQuery(QueryBuilder.build(part), null);

        while (cursor.moveToNext()) {
            MuscleGroup muscleGroup = new MuscleGroup();
            muscleGroup.id = cursor.getLong(cursor.getColumnIndex(MuscleGroupEntry._ID));
            muscleGroup.name = cursor.getString(cursor.getColumnIndex(MuscleGroupEntry.COLUMN_NAME));
            muscleGroup.mapColorRgb = cursor.getString(cursor.getColumnIndex(MuscleGroupEntry.COLUMN_MAP_COLOR_RGB));
            muscleGroup.isAnterior = 1 == cursor.getInt(cursor.getColumnIndex(MuscleGroupEntry.COLUMN_IS_ANTERIOR));
            result.add(muscleGroup);
        }

        cursor.close();

        return result;
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
        cv.put(MuscleGroupEntry.COLUMN_NAME, getName());
        cv.put(MuscleGroupEntry.COLUMN_MAP_COLOR_RGB, getMapColorRgb());
        cv.put(MuscleGroupEntry.COLUMN_IS_ANTERIOR, isAnterior() ? 1 : 0);
    }

    // Parcelable implementation
    private MuscleGroup(Parcel in) {
        id = in.readLong();
        name = in.readString();
        mapColorRgb = in.readString();
        isAnterior = in.readInt() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(mapColorRgb);
        dest.writeInt(isAnterior ? 1 : 0);
    }

    public static final Creator<MuscleGroup> CREATOR = new Creator<MuscleGroup>() {
        @Override
        public MuscleGroup createFromParcel(Parcel in) {
            return new MuscleGroup(in);
        }

        @Override
        public MuscleGroup[] newArray(int size) {
            return new MuscleGroup[size];
        }
    };
}
