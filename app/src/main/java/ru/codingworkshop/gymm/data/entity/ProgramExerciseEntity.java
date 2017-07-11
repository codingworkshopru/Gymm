package ru.codingworkshop.gymm.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import ru.codingworkshop.gymm.data.model.ProgramExercise;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by Радик on 04.06.2017.
 */

@Entity(
        tableName = "ProgramExercise",
        foreignKeys = {
                @ForeignKey(
                        entity = ProgramTrainingEntity.class,
                        parentColumns = "id",
                        childColumns = "programTrainingId",
                        onDelete = CASCADE,
                        onUpdate = CASCADE
                ),
                @ForeignKey(
                        entity = ExerciseEntity.class,
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
public class ProgramExerciseEntity implements ProgramExercise {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long programTrainingId;
    private long exerciseId;
    private int sortOrder;
    private boolean drafting;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public long getProgramTrainingId() {
        return programTrainingId;
    }

    @Override
    public void setProgramTrainingId(long programTrainingId) {
        this.programTrainingId = programTrainingId;
    }

    @Override
    public long getExerciseId() {
        return exerciseId;
    }

    @Override
    public void setExerciseId(long exerciseId) {
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
}
