package ru.codingworkshop.gymm.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;

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
                        entity = ExerciseEntity.class,
                        parentColumns = "id",
                        childColumns = "exerciseId",
                        onDelete = CASCADE,
                        onUpdate = CASCADE
                ),
                @ForeignKey(
                        entity = MuscleGroupEntity.class,
                        parentColumns = "id",
                        childColumns = "muscleGroupId"
        )
})
public class SecondaryMuscleGroupLinkEntity {
    private long exerciseId;
    private long muscleGroupId;

    public SecondaryMuscleGroupLinkEntity(long exerciseId, long muscleGroupId) {
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
}
