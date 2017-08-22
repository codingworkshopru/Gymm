package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.ModelsFixture;

import static org.junit.Assert.assertEquals;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */
public class ProgramTrainingTreeLoaderTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private ProgramTrainingTree tree;
    private ProgramTrainingTreeLoader loader;

    @Before
    public void setUp() throws Exception {
        tree = new ImmutableProgramTrainingTree();
        loader = new ProgramTrainingTreeLoader(tree);

        loader.setParent(ModelsFixture.createLiveProgramTraining(1L, "foo", false));
        loader.setChildren(ModelsFixture.createLiveProgramExercises(1));
        loader.setGrandchildren(ModelsFixture.createLiveProgramSets(3));
        loader.setLiveExercises(ModelsFixture.createLiveExercises("bar", "baz"));
    }

    @Test
    public void load() throws Exception {
        LiveTest.verifyLiveData(loader.load(), b -> b);

        assertEquals(1L, tree.getParent().getId());
        assertEquals(2L, tree.getChildren().get(0).getId());
        assertEquals(3L, tree.getChildren().get(0).getChildren().get(0).getId());
        assertEquals(100L, tree.getChildren().get(0).getExercise().getId());
    }

}