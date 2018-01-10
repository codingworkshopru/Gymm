package ru.codingworkshop.gymm.data.tree.loader.builder;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.tree.node.ImmutableActualTrainingTree;
import ru.codingworkshop.gymm.util.Models;

import static org.junit.Assert.assertEquals;

public class ImmutableActualTrainingTreeBuilderTest {

    @Test
    public void buildTest() {
        ActualTraining actualTraining = Models.createActualTraining(11L, 1L);
        List<ActualExercise> actualExercises = Models.createActualExercises(12L);
        ActualSet actualSet = Models.createActualSet(13L, 12L, 10);
        actualSet.setWeight(2.125);
        List<ActualSet> actualSets = Collections.singletonList(actualSet);

        ImmutableActualTrainingTreeBuilder builder = new ImmutableActualTrainingTreeBuilder(new ImmutableActualTrainingTree());
        builder.setParent(actualTraining);
        builder.setChildren(actualExercises);
        builder.setGrandchildren(actualSets);

        ImmutableActualTrainingTree tree = (ImmutableActualTrainingTree) builder.build();
        assertEquals(actualTraining, tree.getParent());
        assertEquals(actualExercises.get(0), tree.getChildren().get(0).getParent());
        assertEquals(21.25, tree.getChildren().get(0).getVolume(), 0.0);
        assertEquals(actualSets.get(0), tree.getChildren().get(0).getChildren().get(0));
    }
}