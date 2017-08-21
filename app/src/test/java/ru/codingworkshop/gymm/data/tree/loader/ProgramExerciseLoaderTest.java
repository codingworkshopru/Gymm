package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.ModelsFixture;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */
public class ProgramExerciseLoaderTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private ProgramExerciseNode node;
    private ProgramExerciseLoader loader;

    @Before
    public void setUp() {
        node = new ImmutableProgramExerciseNode();
        loader = new ProgramExerciseLoader(node);
    }

    @Test
    public void load() throws Exception {
        loader.setParent(ModelsFixture.createLiveProgramExercise(2L, 1L, false));
        loader.setChildren(LiveDataUtil.getLive(new ArrayList<>()));
        loader.setLiveExerciseGetter(input -> ModelsFixture.createLiveExercise(100L, "foo"));

        LiveTest.verifyLiveData(loader.load(), b -> b);

        assertEquals(100L, node.getExercise().getId());
        assertFalse(node.hasChildren());
    }

}