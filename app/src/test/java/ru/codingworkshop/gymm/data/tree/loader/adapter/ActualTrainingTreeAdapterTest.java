package ru.codingworkshop.gymm.data.tree.loader.adapter;

import com.google.common.collect.Iterables;

import org.junit.Test;

import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramTrainingTree;
import ru.codingworkshop.gymm.util.Models;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Радик on 22.08.2017 as part of the Gymm project.
 */
public class ActualTrainingTreeAdapterTest {
    @Test
    public void build() throws Exception {
        ImmutableProgramTrainingTree programTrainingTree = new ImmutableProgramTrainingTree();
        ProgramTrainingTreeAdapter programTrainingTreeAdapter = new ProgramTrainingTreeAdapter(programTrainingTree);
        programTrainingTreeAdapter.setParent(Models.createProgramTraining(1L, "foo"));
        programTrainingTreeAdapter.setChildren(Models.createProgramExercises(1));
        programTrainingTreeAdapter.setGrandchildren(Models.createProgramSets(1));
        programTrainingTreeAdapter.setExercises(Models.createExercises("bar"));
        programTrainingTreeAdapter.build();

        ActualTrainingTree tree = new ActualTrainingTree();
        ActualTrainingTreeAdapter adapter = new ActualTrainingTreeAdapter(tree, programTrainingTree);

        adapter.setChildren(Models.createActualExercises(12L));
        adapter.setGrandchildren(Models.createActualSets(12L, 13L, 14L));
        adapter.setParent(Models.createActualTraining(11L, 1L));

        adapter.build();

        assertEquals(12L, tree.getChildren().get(0).getParent().getId());
        assertEquals(14L, Iterables.getLast(tree.getChildren().get(0).getChildren()).getId());
        assertEquals(1L, tree.getProgramTraining().getId());
        assertEquals(2L, tree.getChildren().get(0).getProgramExerciseNode().getParent().getId());
    }
}