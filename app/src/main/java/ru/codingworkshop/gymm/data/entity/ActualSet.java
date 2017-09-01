package ru.codingworkshop.gymm.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import javax.annotation.Nullable;

import ru.codingworkshop.gymm.data.entity.common.Model;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by Радик on 04.06.2017.
 */

@Entity(
        tableName = "ActualSet",
        foreignKeys = {
                @ForeignKey(
                        entity = ActualExercise.class,
                        parentColumns = "id",
                        childColumns = "actualExerciseId",
                        onDelete = CASCADE,
                        onUpdate = CASCADE
                )
        },
        indices = @Index("actualExerciseId")
)
public class ActualSet implements Model {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long actualExerciseId;
    private int reps;
    private Double weight;

    public ActualSet(long actualExerciseId, int reps) {
        this.actualExerciseId = actualExerciseId;
        this.reps = reps;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public long getActualExerciseId() {
        return actualExerciseId;
    }

    public void setActualExerciseId(long actualExerciseId) {
        this.actualExerciseId = actualExerciseId;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public @Nullable Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
