package ru.codingworkshop.gymm.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.support.v4.util.LongSparseArray;

import java.util.ArrayList;
import java.util.List;

import ru.codingworkshop.gymm.data.GymContract.MuscleGroupEntry;
import ru.codingworkshop.gymm.data.GymContract.SecondaryMuscleGroupLinkEntry;
import ru.codingworkshop.gymm.data.QueryBuilder;
import ru.codingworkshop.gymm.data.model.base.Model;


/**
 * Created by Радик on 14.02.2017.
 */

public final class MuscleGroup extends Model {
    private long id;
    private String name;
    private String mapColorRgb;
    private boolean isAnterior;

    private static final String TABLE_NAME = MuscleGroupEntry.TABLE_NAME;
    private static final String TAG = MuscleGroup.class.getSimpleName();

    private static final LongSparseArray<List<MuscleGroup>> cache = new LongSparseArray<>();

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

    private static MuscleGroup newInstance(Cursor c) {
        MuscleGroup muscleGroup = new MuscleGroup();

        muscleGroup.id = c.getLong(c.getColumnIndex(MuscleGroupEntry._ID));
        muscleGroup.name = c.getString(c.getColumnIndex(MuscleGroupEntry.COLUMN_NAME));

        int columnIndex = c.getColumnIndex(MuscleGroupEntry.COLUMN_MAP_COLOR_RGB);
        if (columnIndex != -1)
            muscleGroup.mapColorRgb = c.getString(columnIndex);

        columnIndex = c.getColumnIndex(MuscleGroupEntry.COLUMN_IS_ANTERIOR);
        if (columnIndex != -1)
            muscleGroup.isAnterior = 1 == c.getInt(columnIndex);

        return muscleGroup;
    }

    private static List<MuscleGroup> readAll(SQLiteDatabase db, long muscleGroupId) {
        List<MuscleGroup> result = new ArrayList<>();
        String selection = muscleGroupId != 0 ? MuscleGroupEntry._ID + "=" + muscleGroupId : null;

        QueryBuilder.QueryPart part = new QueryBuilder.QueryPart(TABLE_NAME)
                .setSelection(selection)
                .setOrder(MuscleGroupEntry.COLUMN_NAME);

        String sql = QueryBuilder.build(part);
        int sqlHashCode = sql.hashCode();

        if (cache.indexOfKey(sqlHashCode) < 0) {

            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext())
                result.add(newInstance(cursor));
            cursor.close();

            cache.append(sqlHashCode, result);
        } else {
            result = cache.get(sqlHashCode);
//            Log.d(TAG, "readAll: cache used");
        }

        return result;
    }

    public static List<MuscleGroup> read(SQLiteDatabase db) {
        return readAll(db, 0);
    }

    public static MuscleGroup read(SQLiteDatabase db, long muscleGroupId) {
        return readAll(db, muscleGroupId).get(0);
    }

    static List<MuscleGroup> readByExerciseId(SQLiteDatabase db, long exerciseId) {
        List<MuscleGroup> result = new ArrayList<>();

        QueryBuilder.QueryPart musclesPart = new QueryBuilder.QueryPart(MuscleGroup.TABLE_NAME)
                .setColumns(MuscleGroupEntry._ID, MuscleGroupEntry.COLUMN_NAME)
                .setOrder(MuscleGroupEntry.COLUMN_NAME);

        QueryBuilder.QueryPart linkPart = new QueryBuilder.QueryPart(SecondaryMuscleGroupLinkEntry.TABLE_NAME)
                .setThisJoinColumn(SecondaryMuscleGroupLinkEntry.COLUMN_MUSCLE_GROUP_ID)
                .setOtherJoinColumn(MuscleGroupEntry._ID)
                .setSelection(SecondaryMuscleGroupLinkEntry.COLUMN_EXERCISE_ID + "=" + exerciseId);

        String sql = QueryBuilder.build(musclesPart, linkPart);
        int sqlHashCode = sql.hashCode();

        if (cache.indexOfKey(sqlHashCode) < 0) {
            Cursor c = db.rawQuery(sql, null);
            while (c.moveToNext())
                result.add(newInstance(c));
            c.close();

            cache.append(sqlHashCode, result);
        } else {
            result = cache.get(sqlHashCode);
//            Log.d(TAG, "readByExerciseId: cache used");
        }


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
