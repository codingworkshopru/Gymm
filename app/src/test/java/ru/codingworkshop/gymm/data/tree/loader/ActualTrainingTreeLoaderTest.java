package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.stream.Collectors;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ActualTrainingAdapter;
import ru.codingworkshop.gymm.util.Models;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 22.08.2017 as part of the Gymm project.
 */

@RunWith(MockitoJUnitRunner.class)
public class ActualTrainingTreeLoaderTest {
    private ProgramTrainingTree programTree;
    private ActualTrainingTree tree;

    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        programTree = new ImmutableProgramTrainingTree();
        programTree.setParent(Models.createProgramTraining(1L, "foo"));
        programTree.setChildren(Models.createProgramExercises(1).stream().map(e -> {
            ProgramExerciseNode node = programTree.createChildNode(e);
            node.setExercise(Models.createExercise(100L, "bar"));
            node.setChildren(Models.createProgramSets(2L, 1));
            return node;
        }).collect(Collectors.toList()));

        tree = new ActualTrainingTree();
    }

    @Test
    public void load() throws Exception {
        ActualTrainingAdapter adapter = mock(ActualTrainingAdapter.class);
        when(adapter.getParent(11L)).thenReturn(Flowable.just(Models.createActualTraining(11L, 1L)));
        when(adapter.getChildren(11L)).thenReturn(Flowable.just(Models.createActualExercises(12L)));
        when(adapter.getGrandchildren(11L)).thenReturn(Flowable.just(Models.createActualSets(12L, 13L)));

        ProgramTrainingTreeLoader programTrainingLoader = mock(ProgramTrainingTreeLoader.class);
        when(programTrainingLoader.loadById(any(ProgramTrainingTree.class), eq(1L))).thenReturn(Flowable.just(programTree));
        ActualTrainingTreeLoader loader = new ActualTrainingTreeLoader(adapter, programTrainingLoader);

        loader.loadById(tree, 11L).test().assertValue(loadedTree -> {
            assertEquals(11L, tree.getParent().getId());
            assertEquals(1L, tree.getProgramTraining().getId());
            assertEquals(2L, tree.getChildren().get(0).getProgramExerciseNode().getParent().getId());
            assertEquals(3L, tree.getChildren().get(0).getProgramExerciseNode().getChildren().get(0).getId());
            assertEquals(12L, tree.getChildren().get(0).getParent().getId());
            assertEquals(13L, tree.getChildren().get(0).getChildren().get(0).getId());

            return true;
        });
    }
}
