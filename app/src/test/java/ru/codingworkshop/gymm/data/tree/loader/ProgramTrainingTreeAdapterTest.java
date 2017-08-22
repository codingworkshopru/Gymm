package ru.codingworkshop.gymm.data.tree.loader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.util.ModelsFixture;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProgramTrainingTreeAdapterTest {
    @Mock private ProgramTrainingTree tree;
    private ProgramTrainingTreeAdapter adapter;

    @Before
    public void setUp() throws Exception {
        adapter = new ProgramTrainingTreeAdapter(tree);
    }

    @Test
    public void setParent() throws Exception {
        final ProgramTraining training = ModelsFixture.createProgramTraining(1L, "foo");
        adapter.setParent(training);
        verify(tree).setParent(training);
    }

    @Test
    public void build() throws Exception {
        doAnswer(invocation -> {
            ProgramExercise programExercise = invocation.getArgument(0);
            return new ImmutableProgramExerciseNode(programExercise);
        }).when(tree).createChildNode(any());

        adapter.setChildren(ModelsFixture.createProgramExercises(1));
        adapter.setGrandchildren(ModelsFixture.createProgramSets(3));
        adapter.setExercises(ModelsFixture.createExercises("foobar"));
        adapter.build();

        verify(tree).setChildren(argThat(nodes -> {
            final ProgramExerciseNode node = nodes.get(0);
            assertEquals(2L, node.getId());
            assertEquals(3L, node.getChildren().get(0).getId());
            assertEquals(100L, node.getExercise().getId());
            return true;
        }));
    }
}