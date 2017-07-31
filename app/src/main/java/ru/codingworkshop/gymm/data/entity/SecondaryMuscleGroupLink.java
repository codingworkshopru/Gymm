package ru.codingworkshop.gymm.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;

import com.google.common.base.Objects;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by Радик on 22.05.2017.
 */

@Entity(
        tableName = "SecondaryMuscleGroupLink",
        primaryKeys = {"exerciseId", "muscleGroupId"},
        indices = { @Index("exerciseId"), @Index("muscleGroupId") },
        foreignKeys = {
                @ForeignKey(
                        entity = Exercise.class,
                        parentColumns = "id",
                        childColumns = "exerciseId",
                        onDelete = CASCADE,
                        onUpdate = CASCADE
                ),
                @ForeignKey(
                        entity = MuscleGroup.class,
                        parentColumns = "id",
                        childColumns = "muscleGroupId"
        )
})
public class SecondaryMuscleGroupLink {
    private long exerciseId;
    private long muscleGroupId;

    public SecondaryMuscleGroupLink(long exerciseId, long muscleGroupId) {
        this.exerciseId = exerciseId;
        this.muscleGroupId = muscleGroupId;
    }

    public long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public long getMuscleGroupId() {
        return muscleGroupId;
    }

    public void setMuscleGroupId(long muscleGroupId) {
        this.muscleGroupId = muscleGroupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecondaryMuscleGroupLink that = (SecondaryMuscleGroupLink) o;
        return exerciseId == that.exerciseId &&
                muscleGroupId == that.muscleGroupId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(exerciseId, muscleGroupId);
    }
}
