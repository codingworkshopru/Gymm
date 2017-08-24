package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.stream.Collectors;

import ru.codingworkshop.gymm.data.tree.loader.adapter.ActualTrainingTreeAdapter;
import ru.codingworkshop.gymm.data.tree.loader.datasource.ActualTrainingDataSource;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.ModelsFixture;

import static junit.framework.Assert.assertEquals;
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
        programTree.setParent(ModelsFixture.createProgramTraining(1L, "foo"));
        programTree.setChildren(ModelsFixture.createProgramExercises(1).stream().map(e -> {
            ProgramExerciseNode node = programTree.createChildNode(e);
            node.setExercise(ModelsFixture.createExercise(100L, "bar"));
            node.setChildren(ModelsFixture.createProgramSets(1));
            return node;
        }).collect(Collectors.toList()));

        tree = new ActualTrainingTree();
    }

    @Test
    public void load() throws Exception {
        ActualTrainingTreeAdapter adapter = new ActualTrainingTreeAdapter(tree, programTree);
        ActualTrainingDataSource dataSource = mock(ActualTrainingDataSource.class);
        when(dataSource.getParent()).thenReturn(ModelsFixture.createLiveActualTraining(11L, 1L));
        when(dataSource.getChildren()).thenReturn(ModelsFixture.createLiveActualExercises(12L));
        when(dataSource.getGrandchildren()).thenReturn(ModelsFixture.createLiveActualSets(12L, 13L));
        ActualTrainingTreeLoader loader = new ActualTrainingTreeLoader(adapter, dataSource);

        LiveTest.verifyLiveData(loader.load(), b -> b);

        assertEquals(11L, tree.getParent().getId());
        assertEquals(1L, tree.getProgramTraining().getId());
        assertEquals(2L, tree.getChildren().get(0).getProgramExerciseNode().getParent().getId());
        assertEquals(3L, tree.getChildren().get(0).getProgramExerciseNode().getChildren().get(0).getId());
        assertEquals(12L, tree.getChildren().get(0).getParent().getId());
        assertEquals(13L, tree.getChildren().get(0).getChildren().get(0).getId());
    }
}
