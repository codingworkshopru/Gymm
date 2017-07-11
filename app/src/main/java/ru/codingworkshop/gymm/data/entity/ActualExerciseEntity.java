package ru.codingworkshop.gymm.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import javax.annotation.Nullable;
import javax.inject.Inject;

import ru.codingworkshop.gymm.data.model.ActualExercise;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static android.arch.persistence.room.ForeignKey.SET_NULL;

/**
 * Created by Радик on 04.06.2017.
 */

@Entity(
        tableName = "ActualExercise",
        foreignKeys = {
                @ForeignKey(
                        entity = ExerciseEntity.class,
                        parentColumns = "name",
                        childColumns = "exerciseName",
                        onUpdate = CASCADE
                ),
                @ForeignKey(
                        entity = ActualTrainingEntity.class,
                        parentColumns = "id",
                        childColumns = "actualTrainingId",
                        onDelete = CASCADE,
                        onUpdate = CASCADE
                ),
                @ForeignKey(
                        entity = ProgramExerciseEntity.class,
                        parentColumns = "id",
                        childColumns = "programExerciseId",
                        onDelete = SET_NULL,
                        onUpdate = CASCADE
                )
        },
        indices = {
                @Index("exerciseName"),
                @Index("actualTrainingId"),
                @Index("programExerciseId")
        }
)
public class ActualExerciseEntity implements ActualExercise {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String exerciseName;
    private long actualTrainingId;
    private Long programExerciseId;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getExerciseName() {
        return exerciseName;
    }

    @Override
    public void setExerciseName(@NonNull String exerciseName) {
        this.exerciseName = exerciseName;
    }

    @Override
    public long getActualTrainingId() {
        return actualTrainingId;
    }

    @Override
    public void setActualTrainingId(long actualTrainingId) {
        this.actualTrainingId = actualTrainingId;
    }

    @Override
    public @Nullable Long getProgramExerciseId() {
        return programExerciseId;
    }

    @Override
    public void setProgramExerciseId(Long programExerciseId) {
        this.programExerciseId = programExerciseId;
    }
}
