package ru.codingworkshop.gymm.data.model.base;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import ru.codingworkshop.gymm.data.model.field.Field;

/**
 * Created by Радик on 18.02.2017.
 */

public abstract class MutableModel implements Model, Cloneable {

    protected long create(SQLiteDatabase db, String tableName, String parentColumnName, long parentId, Field<Long> idField) {
        if (!isPhantom())
            return -1;

        ContentValues cv = new ContentValues();
        if (!parentColumnName.isEmpty())
            cv.put(parentColumnName, parentId);
        addFieldsToContentValues(cv, false);

        long id = db.insert(tableName, null, cv);

        if (id == -1)
            throw new SQLiteException("Insertion error: " + cv);

        idField.setData(id);
        commit();

        return id;
    }

    protected int update(SQLiteDatabase db, String tableName, Field<Long> idField) {
        if (isPhantom() || !isChanged())
            return 0;

        ContentValues cv = new ContentValues();
        addFieldsToContentValues(cv, true);

        commit();

        return db.update(tableName, cv, idField.getColumnName() + "=?", new String[] {String.valueOf(getId())});
    }

    protected int delete(SQLiteDatabase db, String tableName, Field<Long> idField) {
        if (isPhantom())
            return 0;

        int rowsAffected = db.delete(tableName, idField.getColumnName() + "=?", new String[] {String.valueOf(getId())});

        if (rowsAffected != 0)
            idField.setInitialData(0L);

        return rowsAffected;
    }

    abstract public long create(SQLiteDatabase db, long parentId);
    abstract public int update(SQLiteDatabase db);
    abstract public int delete(SQLiteDatabase db);

    abstract public boolean isChanged();
    abstract protected void commit();
    abstract public String validate();

    @Override
    public MutableModel clone() throws CloneNotSupportedException {
        return (MutableModel) super.clone();
    }

    public boolean isPhantom() {
        return getId() == 0;
    }

    abstract protected void addFieldsToContentValues(ContentValues cv, boolean onlyChanged);
}
