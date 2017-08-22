package ru.codingworkshop.gymm.data.tree;

import org.junit.Test;

import java.lang.reflect.Constructor;

import static java.lang.reflect.Modifier.PRIVATE;
import static junit.framework.Assert.assertEquals;

/**
 * Created by Радик on 22.08.2017 as part of the Gymm project.
 */

public class Preconditions2Test {
    @Test
    public void constructorIsPrivate() {
        for (Constructor<?> c : Preconditions2.class.getDeclaredConstructors()) {
            assertEquals(PRIVATE, c.getModifiers());
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void argIsNotNull() throws Exception {
        Preconditions2.checkIsNull(1);
    }

    @Test
    public void argIsNull() throws Exception {
        Preconditions2.checkIsNull(null);
    }
}
