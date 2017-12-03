package ru.codingworkshop.gymm.util;

import android.support.annotation.NonNull;

import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.entity.common.Named;
import ru.codingworkshop.gymm.data.entity.common.Sortable;

/**
 * Created by Радик on 28.07.2017.
 */
public final class SimpleModel implements Model, Named, Sortable {
    private long id;
    private String name;
    private int sortOrder;

    public SimpleModel(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public SimpleModel(long id, int sortOrder) {
        this.id = id;
        this.sortOrder = sortOrder;
    }

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
    public int getSortOrder() {
        return sortOrder;
    }

    @Override
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleModel model = (SimpleModel) o;

        if (id != model.id) return false;
        if (sortOrder != model.sortOrder) return false;
        return name != null ? name.equals(model.name) : model.name == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + sortOrder;
        return result;
    }
}
