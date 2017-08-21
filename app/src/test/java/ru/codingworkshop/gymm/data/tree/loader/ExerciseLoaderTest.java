package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ru.codingworkshop.gymm.data.tree.node.ExerciseNode;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.ModelsFixture;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */

public class ExerciseLoaderTest {
    private ExerciseNode node;
    private ExerciseLoader loader;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        node = new ExerciseNode();
        loader = new ExerciseLoader(node);
        loader.setParent(ModelsFixture.createLiveExercise(100L, "foo"));
        loader.setChildren(ModelsFixture.createLiveMuscleGroups(900L));
    }

    @Test
    public void load() throws Exception {
        LiveTest.verifyLiveData(loader.load(), b -> b);

        assertEquals(100L, node.getParent().getId());
        assertEquals(900L, node.getChildren().get(0).getId());
    }
}