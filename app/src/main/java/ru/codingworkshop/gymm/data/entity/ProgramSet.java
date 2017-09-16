package ru.codingworkshop.gymm.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;

import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.entity.common.Sortable;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by Радик on 04.06.2017.
 */

@Entity(
        tableName = "ProgramSet",
        foreignKeys = {
                @ForeignKey(
                        entity = ProgramExercise.class,
                        parentColumns = "id",
                        childColumns = "programExerciseId",
                        onDelete = CASCADE,
                        onUpdate = CASCADE
                )
        },
        indices = @Index("programExerciseId")
)
public class ProgramSet implements Model, Sortable, Parcelable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long programExerciseId;
    private int reps;
    private int secondsForRest;
    private int sortOrder;

    public ProgramSet() {
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public long getProgramExerciseId() {
        return programExerciseId;
    }

    public void setProgramExerciseId(long programExerciseId) {
        this.programExerciseId = programExerciseId;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public @Nullable Integer getSecondsForRest() {
        return secondsForRest;
    }

    public void setSecondsForRest(Integer secondsForRest) {
        this.secondsForRest = secondsForRest;
    }

    @Override
    public int getSortOrder() {
        return sortOrder;
    }

    @Override
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProgramSet that = (ProgramSet) o;
        return id == that.id &&
                programExerciseId == that.programExerciseId &&
                reps == that.reps &&
                secondsForRest == that.secondsForRest &&
                sortOrder == that.sortOrder;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, programExerciseId, reps, secondsForRest, sortOrder);
    }

    protected ProgramSet(Parcel in) {
        id = in.readLong();
        programExerciseId = in.readLong();
        reps = in.readInt();
        secondsForRest = in.readInt();
        sortOrder = in.readInt();
    }

    public static final Creator<ProgramSet> CREATOR = new Creator<ProgramSet>() {
        @Override
        public ProgramSet createFromParcel(Parcel in) {
            return new ProgramSet(in);
        }

        @Override
        public ProgramSet[] newArray(int size) {
            return new ProgramSet[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(programExerciseId);
        dest.writeInt(reps);
        dest.writeInt(secondsForRest);
        dest.writeInt(sortOrder);
    }
}
