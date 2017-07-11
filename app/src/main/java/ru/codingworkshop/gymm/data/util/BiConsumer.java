package ru.codingworkshop.gymm.data.util;

/**
 * Created by Радик on 22.06.2017.
 */

@FunctionalInterface
public interface BiConsumer<T, U> {
    void accept(T t, U u);
}
