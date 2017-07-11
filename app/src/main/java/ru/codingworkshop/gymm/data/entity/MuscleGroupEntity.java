package ru.codingworkshop.gymm.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.common.base.Objects;

import ru.codingworkshop.gymm.data.model.MuscleGroup;

/**
 * Created by Радик on 22.05.2017.
 */

@Entity(tableName = "MuscleGroup", indices = @Index(value = "name", unique = true))
public class MuscleGroupEntity implements MuscleGroup {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String mapColorRgb;
    private boolean isAnterior;

    public MuscleGroupEntity(String name) {
        this.name = name;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
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
    public String getMapColorRgb() {
        return mapColorRgb;
    }

    @Override
    public void setMapColorRgb(@NonNull String mapColorRgb) {
        this.mapColorRgb = mapColorRgb;
    }

    @Override
    public boolean isAnterior() {
        return isAnterior;
    }

    @Override
    public void setAnterior(boolean anterior) {
        isAnterior = anterior;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MuscleGroupEntity that = (MuscleGroupEntity) o;
        return id == that.id &&
                Objects.equal(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name);
    }
}
