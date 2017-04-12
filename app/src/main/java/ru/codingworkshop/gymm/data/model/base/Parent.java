package ru.codingworkshop.gymm.data.model.base;

/**
 * Created by Радик on 02.03.2017.
 */
public interface Parent<T extends Model> {
    void addChild(T model);
    void setChild(int index, T model);
    void moveChild(int fromIndex, int toIndex);
    T removeChild(int index);
    int restoreLastRemoved();
    T getChild(int index);
    int childrenCount();
}
