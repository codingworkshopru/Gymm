package ru.codingworkshop.gymm.data.model.field;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import ru.codingworkshop.gymm.data.model.base.Model;

/**
 * Created by Радик on 17.02.2017.
 */

public class Field<T> implements Cloneable, Parcelable {
    private Class<T> type;

    private T data;
    private T changedData;
    private String columnName;

    public Field(String columnName, Class<T> type) {
        this.columnName = columnName;

        this.type = type;
    }

    public Field(String columnName, T initialData) {
        this(columnName, (Class<T>) initialData.getClass());
        data = initialData;
    }

    public void commit() {
        if (changedData == null)
            return;

        data = changedData;
        changedData = null;
    }

    public void setInitialData(T data) {
        this.data = data;
    }

    public void setData(T newData) {
        changedData = data != null && data.equals(newData) ? null : newData;
    }

    public T getData() {
        return isChanged() ? changedData : data;
    }

    public boolean isChanged() {
        return changedData != null;
    }

    public String getColumnName() {
        return columnName;
    }

    @Override
    public Field<T> clone() throws CloneNotSupportedException {
        return (Field<T>) super.clone();
    }

    private Field(Parcel in) {
        type = (Class<T>) in.readSerializable();
        data = (T) in.readValue(type.getClassLoader());
        changedData = (T) in.readValue(type.getClassLoader());
        columnName = in.readString();
    }

    public static final Creator<Field> CREATOR = new Creator<Field>() {
        @Override
        public Field createFromParcel(Parcel in) {
            return new Field(in);
        }

        @Override
        public Field[] newArray(int size) {
            return new Field[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(type);
        dest.writeValue(data);
        dest.writeValue(changedData);
        dest.writeString(columnName);
    }

    public static void addToValues(ContentValues cv, Field field, boolean onlyChanged) {
        if (onlyChanged && !field.isChanged())
            return;

        String column = field.getColumnName();
        Object data = field.getData();

        if (data instanceof Long)
            cv.put(column, (Long) data);
        else if (data instanceof Integer)
            cv.put(column, (Integer) data);
        else if (data instanceof String)
            cv.put(column, data.toString());
        else if (data instanceof Model)
            cv.put(column, ((Model)data).getId());
    }
}
