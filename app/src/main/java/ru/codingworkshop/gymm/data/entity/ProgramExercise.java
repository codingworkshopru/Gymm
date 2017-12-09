package ru.codingworkshop.gymm.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by Радик on 04.06.2017.
 */

@Entity(
        tableName = "ProgramExercise",
        foreignKeys = {
                @ForeignKey(
                        entity = ProgramTraining.class,
                        parentColumns = "id",
                        childColumns = "programTrainingId",
                        onDelete = CASCADE,
                        onUpdate = CASCADE
                ),
                @ForeignKey(
                        entity = Exercise.class,
                        parentColumns = "id",
                        childColumns = "exerciseId",
                        onDelete = CASCADE,
                        onUpdate = CASCADE
                )
        },
        indices = {
                @Index("programTrainingId"),
                @Index("exerciseId")
        }
)
public class ProgramExercise implements IProgramExercise, Cloneable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long programTrainingId;
    private Long exerciseId; // TODO entity validations in repository; make it long, but not Long
    private int sortOrder;
    private boolean drafting;

    public ProgramExercise() {
    }

    public ProgramExercise(@NonNull ProgramExercise that) {
        Preconditions.checkNotNull(that, "parameter of copy constructor must be non null");

        this.id = that.id;
        this.programTrainingId = that.programTrainingId;
        this.exerciseId = that.exerciseId;
        this.sortOrder = that.sortOrder;
        this.drafting = that.drafting;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public long getProgramTrainingId() {
        return programTrainingId;
    }

    public void setProgramTrainingId(long programTrainingId) {
        this.programTrainingId = programTrainingId;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
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
    public boolean isDrafting() {
        return drafting;
    }

    @Override
    public void setDrafting(boolean drafting) {
        this.drafting = drafting;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProgramExercise that = (ProgramExercise) o;

        if (id != that.id) return false;
        if (programTrainingId != that.programTrainingId) return false;
        if (sortOrder != that.sortOrder) return false;
        return exerciseId != null ? exerciseId.equals(that.exerciseId) : that.exerciseId == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (programTrainingId ^ (programTrainingId >>> 32));
        result = 31 * result + (exerciseId != null ? exerciseId.hashCode() : 0);
        result = 31 * result + sortOrder;
        return result;
    }
}
