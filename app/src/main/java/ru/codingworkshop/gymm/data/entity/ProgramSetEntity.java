package ru.codingworkshop.gymm.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import ru.codingworkshop.gymm.data.model.ProgramSet;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by Радик on 04.06.2017.
 */

@Entity(
        tableName = "ProgramSet",
        foreignKeys = {
                @ForeignKey(
                        entity = ProgramExerciseEntity.class,
                        parentColumns = "id",
                        childColumns = "programExerciseId",
                        onDelete = CASCADE,
                        onUpdate = CASCADE
                )
        },
        indices = @Index("programExerciseId")
)
public class ProgramSetEntity implements ProgramSet {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long programExerciseId;
    private int reps;
    private int secondsForRest;
    private int sortOrder;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public long getProgramExerciseId() {
        return programExerciseId;
    }

    @Override
    public void setProgramExerciseId(long programExerciseId) {
        this.programExerciseId = programExerciseId;
    }

    @Override
    public int getReps() {
        return reps;
    }

    @Override
    public void setReps(int reps) {
        this.reps = reps;
    }

    @Override
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
}
