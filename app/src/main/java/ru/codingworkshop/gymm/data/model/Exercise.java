package ru.codingworkshop.gymm.data.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ru.codingworkshop.gymm.data.GymContract;

/**
 * Created by Радик on 14.02.2017.
 */

public final class Exercise implements Parcelable {
    private long id;
    private MuscleGroup primaryMuscleGroup;
    private String name;
    private boolean isWithWeight;
    private String video;
    private List<MuscleGroup> secondaryMuscles;

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

    public static Exercise createFromCursor(Cursor c) {
        Exercise exercise = new Exercise();
        exercise.setId(c.getLong(c.getColumnIndex(GymContract.ExerciseEntry._ID)));
        exercise.setName(c.getString(c.getColumnIndex(GymContract.ExerciseEntry.COLUMN_NAME)));
        return exercise;
    }

    public static Exercise read(SQLiteDatabase db, long exerciseId) {
        Cursor cursor = db.query(
                GymContract.ExerciseEntry.TABLE_NAME,
                new String[] {GymContract.ExerciseEntry._ID, GymContract.ExerciseEntry.COLUMN_NAME},
                GymContract.ExerciseEntry._ID + "=" + exerciseId,
                null,
                null,
                null,
                null
        );
        cursor.moveToNext();

        Exercise exercise = createFromCursor(cursor);

        cursor.close();
        return exercise;
    }

    public static List<Exercise> read(SQLiteDatabase db) {
        Cursor cursor = db.query(
                GymContract.ExerciseEntry.TABLE_NAME,
                new String[] {GymContract.ExerciseEntry._ID, GymContract.ExerciseEntry.COLUMN_NAME},
                null,
                null,
                null,
                null,
                null
        );

        List<Exercise> result = new LinkedList<>();
        while (cursor.moveToNext())
            result.add(createFromCursor(cursor));

        cursor.close();

        return result;
    }
}
