package ru.codingworkshop.gymm.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import javax.annotation.Nullable;

import ru.codingworkshop.gymm.data.entity.common.Model;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static android.arch.persistence.room.ForeignKey.SET_NULL;

/**
 * Created by Радик on 04.06.2017.
 */

@Entity(
        tableName = "ActualExercise",
        foreignKeys = {
                @ForeignKey(
                        entity = Exercise.class,
                        parentColumns = "name",
                        childColumns = "exerciseName",
                        onUpdate = CASCADE
                ),
                @ForeignKey(
                        entity = ActualTraining.class,
                        parentColumns = "id",
                        childColumns = "actualTrainingId",
                        onDelete = CASCADE,
                        onUpdate = CASCADE
                ),
                @ForeignKey(
                        entity = ProgramExercise.class,
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
public class ActualExercise implements Model {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String exerciseName;
    private long actualTrainingId;
    private Long programExerciseId;

    public ActualExercise(String exerciseName, long actualTrainingId, long programExerciseId) {
        this.exerciseName = exerciseName;
        this.actualTrainingId = actualTrainingId;
        this.programExerciseId = programExerciseId;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(@NonNull String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public long getActualTrainingId() {
        return actualTrainingId;
    }

    public void setActualTrainingId(long actualTrainingId) {
        this.actualTrainingId = actualTrainingId;
    }

    public @Nullable Long getProgramExerciseId() {
        return programExerciseId;
    }

    public void setProgramExerciseId(Long programExerciseId) {
        this.programExerciseId = programExerciseId;
    }
}
