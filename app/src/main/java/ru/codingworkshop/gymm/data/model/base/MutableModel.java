package ru.codingworkshop.gymm.data.model.base;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Радик on 18.02.2017.
 */

public abstract class MutableModel implements Model, Cloneable {
//    MutableModel() {}

    abstract public long getId();

    abstract public long create(SQLiteDatabase db, long parentId);
//    abstract public MutableModel read(SQLiteDatabase db, long id);
    abstract public int update(SQLiteDatabase db);
    abstract public int delete(SQLiteDatabase db);

    abstract public boolean isChanged();
    abstract public String validate();
    protected abstract void commit();

    @Override
    public MutableModel clone() throws CloneNotSupportedException {
        return (MutableModel) super.clone();
    };

    public boolean isPhantom() {
        return getId() == 0;
    };

    abstract protected void addFieldsToContentValues(ContentValues cv, boolean onlyChanged);

}
