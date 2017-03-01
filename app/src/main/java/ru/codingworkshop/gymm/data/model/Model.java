package ru.codingworkshop.gymm.data.model;

import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import ru.codingworkshop.gymm.data.model.field.Field;

/**
 * Created by Радик on 18.02.2017.
 */

public abstract class Model implements Cloneable, Parcelable {
    protected Field[] fields;

    abstract public long getId();

    abstract public boolean isChanged();

    abstract public long create(SQLiteDatabase db, long parentId);
//    abstract public Model read(SQLiteDatabase db, long id);
    abstract public int update(SQLiteDatabase db);
    abstract public int delete(SQLiteDatabase db);

    Model() {}

    protected void commit() {
        for (Field f : fields)
            if (f.isChanged())
                f.commit();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Model cloned = (Model) super.clone();
        cloned.fields = new Field[fields.length];
        for (int i = 0; i < fields.length; i++)
            cloned.fields[i] = (Field) fields[i].clone();
        return cloned;
    }

    Model(Parcel in) {
        fields = in.createTypedArray(Field.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static Creator<? extends Model> CREATOR = null;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(fields, flags);
    }

    public interface Orderable {
        void setOrder(int order);
        int getOrder();
    }

    public interface Parent<T extends Model> {
        void addChild(T model);
        void setChild(int index, T model);
        void moveChild(int fromIndex, int toIndex);
        T removeChild(int index);
        T getChild(int index);
        int childrenCount();
    }
}
