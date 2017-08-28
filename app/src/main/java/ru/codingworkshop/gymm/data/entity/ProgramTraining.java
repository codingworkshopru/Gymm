package ru.codingworkshop.gymm.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.codingworkshop.gymm.data.entity.common.Draftable;
import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.entity.common.Named;

/**
 * Created by Радик on 04.06.2017.
 */

@Entity(tableName = "ProgramTraining", indices = @Index(value = "name", unique = true))
public class ProgramTraining implements Model, Named, Draftable {
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

    public @Nullable Integer getWeekday() {
        return weekday;
    }

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
