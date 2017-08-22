package ru.codingworkshop.gymm.data.tree.loader;

import com.google.common.collect.Iterables;

import org.junit.Test;

import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramTrainingTree;
import ru.codingworkshop.gymm.util.ModelsFixture;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Радик on 22.08.2017 as part of the Gymm project.
 */
public class ActualTrainingTreeAdapterTest {
    @Test
    public void build() throws Exception {
        ImmutableProgramTrainingTree programTrainingTree = new ImmutableProgramTrainingTree();
        ProgramTrainingTreeAdapter programTrainingTreeAdapter = new ProgramTrainingTreeAdapter(programTrainingTree);
        programTrainingTreeAdapter.setParent(ModelsFixture.createProgramTraining(1L, "foo"));
        programTrainingTreeAdapter.setChildren(ModelsFixture.createProgramExercises(1));
        programTrainingTreeAdapter.setGrandchildren(ModelsFixture.createProgramSets(1));
        programTrainingTreeAdapter.setExercises(ModelsFixture.createExercises("bar"));
        programTrainingTreeAdapter.build();

        ActualTrainingTree tree = new ActualTrainingTree();
        ActualTrainingTreeAdapter adapter = new ActualTrainingTreeAdapter(tree, programTrainingTree);

        adapter.setChildren(ModelsFixture.createActualExercises(12L));
        adapter.setGrandchildren(ModelsFixture.createActualSets(12L, 13L, 14L));
        adapter.setParent(ModelsFixture.createActualTraining(11L, 1L));

        adapter.build();

        assertEquals(12L, tree.getChildren().get(0).getParent().getId());
        assertEquals(14L, Iterables.getLast(tree.getChildren().get(0).getChildren()).getId());
        assertEquals(1L, tree.getProgramTraining().getId());
        assertEquals(2L, tree.getChildren().get(0).getProgramExerciseNode().getParent().getId());
    }
}