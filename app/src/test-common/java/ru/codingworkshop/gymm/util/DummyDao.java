package ru.codingworkshop.gymm.util;

import java.util.Collection;
import java.util.List;

/**
 * Created by Радик on 31.07.2017 as part of the Gymm project.
 */
public interface DummyDao<T> {
    Long insert(T t);

    List<Long> insert(Collection<T> tt);

    int update(T t);

    int update(Collection<T> t);

    int delete(T t);

    int delete(Collection<T> t);
}
