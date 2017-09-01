package ru.codingworkshop.gymm.data.tree.loader.builder;

import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.node.BaseNode;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.util.Models;

import static org.junit.Assert.assertEquals;

/**
 * Created by Радик on 25.08.2017 as part of the Gymm project.
 */
public class ProgramTrainingTreeBuilderTest {
    @Test
    public void build() throws Exception {
        final ProgramTraining programTraining = Models.createProgramTraining(1L, "foo");
        final List<ProgramExercise> programExercises = Models.createProgramExercises(1);
        final List<ProgramSet> programSets = Models.createProgramSets(2L, 1);
        final List<Exercise> exercises = Models.createExercises("exercises");

        ProgramTrainingTree tree = (ProgramTrainingTree) new ProgramTrainingTreeBuilder(new ImmutableProgramTrainingTree())
                .setExercises(exercises)
                .setParent(programTraining)
                .setChildren(programExercises)
                .setGrandchildren(programSets)
                .build();

        assertEquals(programTraining, tree.getParent());
        assertEquals(programExercises, tree.getChildren().stream().map(BaseNode::getParent).collect(Collectors.toList()));
        assertEquals(programSets, tree.getChildren().get(0).getChildren());
        assertEquals(exercises, tree.getChildren().stream().map(ProgramExerciseNode::getExercise).collect(Collectors.toList()));
    }
}