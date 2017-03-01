package ru.codingworkshop.gymm.data.model.field;

import android.os.Parcel;
import android.os.Parcelable;

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
        data = changedData;
        changedData = null;
    }

    public void setInitialData(T data) {
        this.data = data;
    }

    public void setData(T newData) {
        if (data == null || !data.equals(newData))
            changedData = newData;
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
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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
}
