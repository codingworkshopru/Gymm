package ru.codingworkshop.gymm.data.util;

/**
 * Created by Радик on 28.07.2017.
 */

@FunctionalInterface
public interface Consumer<T> {
    void accept(T t);
}
