package ru.codingworkshop.gymm.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import javax.annotation.Nullable;

import ru.codingworkshop.gymm.data.model.ActualSet;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by Радик on 04.06.2017.
 */

@Entity(
        tableName = "ActualSet",
        foreignKeys = {
                @ForeignKey(
                        entity = ActualExerciseEntity.class,
                        parentColumns = "id",
                        childColumns = "actualExerciseId",
                        onDelete = CASCADE,
                        onUpdate = CASCADE
                )
        },
        indices = @Index("actualExerciseId")
)
public class ActualSetEntity implements ActualSet {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long actualExerciseId;
    private int reps;
    private double weight;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public long getActualExerciseId() {
        return actualExerciseId;
    }

    @Override
    public void setActualExerciseId(long actualExerciseId) {
        this.actualExerciseId = actualExerciseId;
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
    public @Nullable Double getWeight() {
        return weight;
    }

    @Override
    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
