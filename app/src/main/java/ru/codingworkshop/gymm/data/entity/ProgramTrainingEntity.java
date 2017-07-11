package ru.codingworkshop.gymm.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.codingworkshop.gymm.data.model.ProgramTraining;

/**
 * Created by Радик on 04.06.2017.
 */

@Entity(tableName = "ProgramTraining", indices = @Index(value = "name", unique = true))
public class ProgramTrainingEntity implements ProgramTraining {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private int weekday;
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
    public String getName() {
        return name;
    }

    @Override
    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Override
    public @Nullable Integer getWeekday() {
        return weekday;
    }

    @Override
    public void setWeekday(Integer weekday) {
        this.weekday = weekday;
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
