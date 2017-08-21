package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.util.LiveTest;

import static org.junit.Assert.assertEquals;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */

public class SetAndRemoveTest {
    private SetAndRemove setAndRemove;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        setAndRemove = new SetAndRemove();
    }

    @Test
    public void loadAndGet() throws Exception {
        final AtomicLong result = new AtomicLong();
        final AtomicReference<String> result2 = new AtomicReference<>();
        setAndRemove.ok(LiveDataUtil.getLive(1L), result::set);
        setAndRemove.ok(LiveDataUtil.getLive("foo"), result2::set);

        LiveTest.verifyLiveData(setAndRemove.getLoaded(), b -> b);

        assertEquals(1L, result.get());
        assertEquals("foo", result2.get());
    }

}