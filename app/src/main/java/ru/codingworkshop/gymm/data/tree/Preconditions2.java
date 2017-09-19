package ru.codingworkshop.gymm.data.tree;

/**
 * Created by Радик on 22.08.2017 as part of the Gymm project.
 */

public final class Preconditions2 {
    private Preconditions2() {
    }

    public static void checkIsNull(Object o) {
        checkIsNull(o, null);
    }

    public static void checkIsNull(Object o, String message) {
        if (o != null) throw new UnsupportedOperationException(message);
    }
}
