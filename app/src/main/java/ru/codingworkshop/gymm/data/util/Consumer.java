package ru.codingworkshop.gymm.data.util;

import android.support.annotation.Nullable;

/**
 * Created by Радик on 28.07.2017.
 */

@FunctionalInterface
public interface Consumer<T> {
    void accept(@Nullable T t);
}
