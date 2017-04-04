package ru.codingworkshop.gymm.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ru.codingworkshop.gymm.data.GymContract.ExerciseEntry;
import ru.codingworkshop.gymm.data.GymContract.SecondaryMuscleGroupLinkEntry;
import ru.codingworkshop.gymm.data.QueryBuilder;
import ru.codingworkshop.gymm.data.model.base.MutableModel;

/**
 * Created by Радик on 14.02.2017.
 */

public final class Exercise extends MutableModel {
    private long id;
    private MuscleGroup primaryMuscleGroup;
    private String name;
    private boolean isWithWeight;
    private String video;
    private List<MuscleGroup> secondaryMuscles;

    private static final String TABLE_NAME = ExerciseEntry.TABLE_NAME;

    public Exercise() {

    }

    public List<MuscleGroup> getSecondaryMuscles() {
        return secondaryMuscles;
    }

    public void setSecondaryMuscles(List<MuscleGroup> secondaryMuscles) {
        this.secondaryMuscles = secondaryMuscles;
    }

    public void addSecondaryMuscles(MuscleGroup muscles) {
        if (secondaryMuscles == null) {
            secondaryMuscles = new LinkedList<>();
        }
        secondaryMuscles.add(muscles);
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isWithWeight() {
        return isWithWeight;
    }

    public void setWithWeight(boolean withWeight) {
        isWithWeight = withWeight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MuscleGroup getPrimaryMuscleGroup() {
        return primaryMuscleGroup;
    }

    public void setPrimaryMuscleGroup(MuscleGroup primaryMuscleGroup) {
        this.primaryMuscleGroup = primaryMuscleGroup;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public boolean hasVideo() {
        return video != null && !video.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Exercise && getId() == ((Exercise)obj).getId();
    }

    private static Exercise newInstance(Cursor c) {
        Exercise exercise = new Exercise();
        exercise.setId(c.getLong(c.getColumnIndex(ExerciseEntry._ID)));
        exercise.setName(c.getString(c.getColumnIndex(ExerciseEntry.COLUMN_NAME)));

        int columnIndex = c.getColumnIndex(ExerciseEntry.COLUMN_YOUTUBE_VIDEO);
        if (columnIndex != -1)
            exercise.setVideo(c.getString(columnIndex));

        return exercise;
    }

    private static List<Exercise> newListOfInstances(Cursor c, SQLiteDatabase db) {
        List<Exercise> result = new ArrayList<>();

        if (c == null)
            return result;

        while (c.moveToNext()) {
            Exercise exercise = newInstance(c);

            long primaryMuscleId = c.getLong(c.getColumnIndex(ExerciseEntry.COLUMN_PRIMARY_MUSCLE_GROUP_ID));
            exercise.primaryMuscleGroup = MuscleGroup.read(db, primaryMuscleId);

            exercise.setSecondaryMuscles(MuscleGroup.readByExerciseId(db, exercise.getId()));

            result.add(exercise);
        }

        return result;
    }

    /**
     * Reads data from the database and makes a list with models.
     * @param db opened database object.
     * @param id id of the exercise to readAll from DB. If 0 all records will be loaded.
     * @return list of exercises including empty list.
     */
    private static List<Exercise> read(SQLiteDatabase db, String column, long id) {
        String selection = null;
        String[] selectionArgs = null;

        if (column != null && id != 0) {
            selection = column + "=?";
            selectionArgs = new String[] {String.valueOf(id)};
        }

        QueryBuilder.QueryPart exercisesPart = new QueryBuilder.QueryPart(ExerciseEntry.TABLE_NAME)
                .setSelection(selection)
                .setOrder(ExerciseEntry.COLUMN_NAME);

        Cursor c = db.rawQuery(QueryBuilder.build(exercisesPart), selectionArgs);

        List<Exercise> result = newListOfInstances(c, db);

        c.close();

        return result;
    }

    public static Exercise readOne(SQLiteDatabase db, long exerciseId) {
        return read(db, ExerciseEntry._ID, exerciseId).get(0);
    }

    public static List<Exercise> readAll(SQLiteDatabase db) {
        return read(db, null, 0);
    }

    public static List<Exercise> readByPrimaryMuscles(SQLiteDatabase db, long muscleGroupId) {
        return read(db, ExerciseEntry.COLUMN_PRIMARY_MUSCLE_GROUP_ID, muscleGroupId);
    }

    public static List<Exercise> readBySecondaryMuscles(SQLiteDatabase db, long muscleGroupId) {
        List<Exercise> result;

        String[] selectionArgs = {String.valueOf(muscleGroupId)};

        QueryBuilder.QueryPart exercisesWithSecondaryMuscles = new QueryBuilder.QueryPart(ExerciseEntry.TABLE_NAME)
                .setOrder(ExerciseEntry.COLUMN_NAME);

        QueryBuilder.QueryPart exercisesMusclesLink = new QueryBuilder.QueryPart(SecondaryMuscleGroupLinkEntry.TABLE_NAME)
                .setThisJoinColumn(SecondaryMuscleGroupLinkEntry.COLUMN_EXERCISE_ID)
                .setOtherJoinColumn(ExerciseEntry._ID)
                .setSelection(SecondaryMuscleGroupLinkEntry.COLUMN_MUSCLE_GROUP_ID + "=?");

        Cursor c = db.rawQuery(QueryBuilder.build(exercisesWithSecondaryMuscles, exercisesMusclesLink), selectionArgs);

        result = newListOfInstances(c, db);

        return result;
    }

    @Override
    public long create(SQLiteDatabase db, long parentId) {
        id = super.create(db, TABLE_NAME, null, 0, null);

        if (secondaryMuscles != null) {
            ContentValues cv;
            for (MuscleGroup mg : secondaryMuscles) {
                cv = new ContentValues();
                cv.put(SecondaryMuscleGroupLinkEntry.COLUMN_EXERCISE_ID, id);
                cv.put(SecondaryMuscleGroupLinkEntry.COLUMN_MUSCLE_GROUP_ID, mg.getId());
                db.insert(SecondaryMuscleGroupLinkEntry.TABLE_NAME, null, cv);
            }
        }

        return id;
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
        cv.put(ExerciseEntry.COLUMN_PRIMARY_MUSCLE_GROUP_ID, getPrimaryMuscleGroup().getId());
        cv.put(ExerciseEntry.COLUMN_NAME, getName());
        cv.put(ExerciseEntry.COLUMN_YOUTUBE_VIDEO, getVideo());
    }

    // Parcelable implementation
    //-----------------------------------------
    private Exercise(Parcel in) {
        id = in.readLong();
        primaryMuscleGroup = in.readParcelable(MuscleGroup.class.getClassLoader());
        name = in.readString();
        isWithWeight = in.readByte() != 0;
        video = in.readString();
        ArrayList<MuscleGroup> muscleGroupsArrayList = in.createTypedArrayList(MuscleGroup.CREATOR);
        if (muscleGroupsArrayList != null && muscleGroupsArrayList.size() > 0)
            secondaryMuscles = new LinkedList<>(muscleGroupsArrayList);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeParcelable(primaryMuscleGroup, flags);
        dest.writeString(name);
        dest.writeByte((byte) (isWithWeight ? 1 : 0));
        dest.writeString(video);
        dest.writeTypedList(secondaryMuscles);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };
    //-----------------------------------------
}
