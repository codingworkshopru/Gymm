package ru.codingworkshop.gymm.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ru.codingworkshop.gymm.data.GymContract.ExerciseEntry;
import ru.codingworkshop.gymm.data.GymContract.SecondaryMuscleGroupLinkEntry;
import ru.codingworkshop.gymm.data.QueryBuilder;
import ru.codingworkshop.gymm.data.model.base.Model;

/**
 * Created by Радик on 14.02.2017.
 */

public final class Exercise extends Model {
    private long id;
    private MuscleGroup primaryMuscleGroup;
    private String name;
    private boolean isWithWeight;
    private int difficulty;
    private String video;
    private String steps;
    private String advices;
    private String caution;
    private String variations;
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

    public MuscleGroup getPrimaryMuscleGroup() {
        return primaryMuscleGroup;
    }

    public void setPrimaryMuscleGroup(MuscleGroup primaryMuscleGroup) {
        this.primaryMuscleGroup = primaryMuscleGroup;
    }

    public String getName() {
        return name;
    }

    public boolean isWithWeight() {
        return isWithWeight;
    }

    public void setWithWeight(boolean withWeight) {
        isWithWeight = withWeight;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
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

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getAdvices() {
        return advices;
    }

    public void setAdvices(String advices) {
        this.advices = advices;
    }

    public String getCaution() {
        return caution;
    }

    public void setCaution(String caution) {
        this.caution = caution;
    }

    public String getVariations() {
        return variations;
    }

    public void setVariations(String variations) {
        this.variations = variations;
    }


    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Exercise && getId() == ((Exercise)obj).getId();
    }

    private Exercise(Cursor c) {
        this();

        if (c.isClosed() || c.isAfterLast() || c.isBeforeFirst())
            throw new CursorIndexOutOfBoundsException("Provided cursor is in wrong state");

        setId(c.getLong(c.getColumnIndex(ExerciseEntry._ID)));
        setName(c.getString(c.getColumnIndex(ExerciseEntry.COLUMN_NAME)));
        setWithWeight(c.getInt(c.getColumnIndex(ExerciseEntry.COLUMN_IS_WITH_WEIGHT)) == 1);
        setDifficulty(c.getInt(c.getColumnIndex(ExerciseEntry.COLUMN_DIFFICULTY)));
        setVideo(c.getString(c.getColumnIndex(ExerciseEntry.COLUMN_YOUTUBE_VIDEO)));
        setSteps(c.getString(c.getColumnIndex(ExerciseEntry.COLUMN_STEPS)));
        setAdvices(c.getString(c.getColumnIndex(ExerciseEntry.COLUMN_ADVICES)));
        setCaution(c.getString(c.getColumnIndex(ExerciseEntry.COLUMN_CAUTION)));
        setVariations(c.getString(c.getColumnIndex(ExerciseEntry.COLUMN_VARIATIONS)));
    }

    private static List<Exercise> newListOfInstances(Cursor c, SQLiteDatabase db) {
        List<Exercise> result = new ArrayList<>();

        if (c == null)
            return result;

        while (c.moveToNext()) {
            Exercise exercise = new Exercise(c);

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
    private static List<Exercise> read(SQLiteDatabase db, String selectionColumn, long id) {
        String selection = null;
        String[] selectionArgs = null;

        if (selectionColumn != null && id != 0) {
            selection = selectionColumn + "=?";
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
        cv.put(ExerciseEntry.COLUMN_IS_WITH_WEIGHT, isWithWeight() ? 1 : 0);
        cv.put(ExerciseEntry.COLUMN_DIFFICULTY, getDifficulty());
        cv.put(ExerciseEntry.COLUMN_YOUTUBE_VIDEO, getVideo());
        cv.put(ExerciseEntry.COLUMN_STEPS, getSteps());
        cv.put(ExerciseEntry.COLUMN_ADVICES, getAdvices());
        cv.put(ExerciseEntry.COLUMN_CAUTION, getCaution());
        cv.put(ExerciseEntry.COLUMN_VARIATIONS, getVariations());
    }

    // Parcelable implementation
    //-----------------------------------------
    private Exercise(Parcel in) {
        id = in.readLong();
        primaryMuscleGroup = in.readParcelable(MuscleGroup.class.getClassLoader());
        name = in.readString();
        isWithWeight = in.readByte() != 0;
        difficulty = in.readInt();
        video = in.readString();
        steps = in.readString();
        advices = in.readString();
        caution = in.readString();
        variations = in.readString();
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
        dest.writeInt(difficulty);
        dest.writeString(video);
        dest.writeString(steps);
        dest.writeString(advices);
        dest.writeString(caution);
        dest.writeString(variations);
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
