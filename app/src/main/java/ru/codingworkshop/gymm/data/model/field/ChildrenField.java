package ru.codingworkshop.gymm.data.model.field;

/**
 * Created by Радик on 17.02.2017.
 */

import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.LinkedList;
import java.util.List;

import ru.codingworkshop.gymm.data.model.base.MutableModel;
import ru.codingworkshop.gymm.data.model.base.Orderable;

public class ChildrenField<T extends MutableModel> implements Cloneable, Parcelable {
    private Class<T> type;
    private List<T> data;
    private List<T> changedData;
    private T lastRemoved;

    private static final String TAG = ChildrenField.class.getSimpleName();

    public ChildrenField(Class<T> type) {
        this.type = type;
        data = createList();
        changedData = createList();
    }

    public void setInitialList(List<T> list) {
        data = copyList(list);
        changedData = copyList(list);
    }

    public void add(T element) {
        if (element == null)
            throw new IllegalArgumentException("Element is null");

        if (element instanceof Orderable)
            ((Orderable) element).setOrder(changedData.size());

        changedData.add(element);
    }

    public void set(int index, T element) {
        if (element == null)
            throw new IllegalArgumentException("Element is null");

        if (index < 0 || index >= changedData.size())
            throw new IndexOutOfBoundsException("Index out of bounds: ");

        changedData.set(index, element);
    }

    public T remove(int index) {
        if (index >= size())
            return null;

        T removed = changedData.remove(index);
        lastRemoved = removed;
        reorder();
        return removed;
    }

    private void insert(int index, T element) {
        changedData.add(index, element);
        reorder();
    }

    public int restoreLastRemoved() {
        if (lastRemoved == null) return -1;

        if (lastRemoved instanceof Orderable) {
            int order = ((Orderable) lastRemoved).getOrder();
            insert(order, lastRemoved);
            return order;
        } else {
            add(lastRemoved);
            return size() - 1;
        }
    }

    public T get(int index) {
        if (index < 0 || index >= changedData.size())
            throw new IndexOutOfBoundsException("Index out of bounds: ");

        T result = changedData.get(index);

        if (result instanceof Orderable && ((Orderable) result).getOrder() == -1)
            throw new RuntimeException("Invalid order");

        return result;
    }

    public void move(int fromIndex, int toIndex) {
        if (fromIndex >= size() || fromIndex < 0 || toIndex >= size() || toIndex < 0)
            return;

        T removed = changedData.remove(fromIndex);
        changedData.add(toIndex, removed);
        reorder();
    }

    public int size() {
        return changedData != null ? changedData.size() : 0;
    }

    public boolean isChanged() {
        if (data.size() != changedData.size())
            return true;

        for (T element : changedData)
            if (element.getId() == 0 || element.isChanged())
                return true;

        return false;
    }

    private void reorder() {
        int i = 0;
        for (T element : changedData)
            if (element instanceof Orderable)
                ((Orderable) element).setOrder(i++);
    }

    public void save(SQLiteDatabase db, long parentId) {
        if (isChanged()) {
            for (T element : newElements())
                element.create(db, parentId);

            for (T element : removedElements())
                element.delete(db);

            for (T element : changedElements())
                element.update(db);

            setInitialList(changedData);
        }
    }

    private List<T> newElements() {
        List<T> result = createList();

        for (T element : changedData)
            if (element.getId() == 0)
                result.add(element);

        return result;
    }

    private List<T> changedElements() {
        List<T> result = createList();

        for (T element : changedData)
            if (element.isChanged())
                result.add(element);

        return result;
    }

    private List<T> removedElements() {
        List<T> result = createList();

        for (T oldElement : data) {
            boolean found = false;
            for (T element : changedData)
                if (oldElement.getId() == element.getId()) {
                    found = true;
                    break;
                }

            if (!found)
                result.add(oldElement);
        }

        return result;
    }

    private List<T> createList() {
        return new LinkedList<>();
    }

    private List<T> copyList(List<T> source) {
        List<T> copied = createList();
        try {
            for (T element : source)
                copied.add((T) element.clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return copied;
    }

    @Override
    public ChildrenField<T> clone() throws CloneNotSupportedException {
        ChildrenField<T> cloned = (ChildrenField<T>) super.clone();
        cloned.data = copyList(data);
        cloned.changedData = copyList(changedData);
        return cloned;
    }

    private ChildrenField(Parcel in) {
        type = (Class<T>) in.readSerializable();
        data = createList();
        changedData = createList();
        try {
            Creator<T> creator = (Creator<T>) type.getField("CREATOR").get(null);
            in.readTypedList(data, creator);
            in.readTypedList(changedData, creator);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final Creator<ChildrenField> CREATOR = new Creator<ChildrenField>() {
        @Override
        public ChildrenField createFromParcel(Parcel in) {
            return new ChildrenField(in);
        }

        @Override
        public ChildrenField[] newArray(int size) {
            return new ChildrenField[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(type);
        dest.writeTypedList(data);
        dest.writeTypedList(changedData);
    }
}
