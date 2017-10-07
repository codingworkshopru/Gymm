package ru.codingworkshop.gymm.util;

import android.support.test.espresso.core.internal.deps.guava.base.Preconditions;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.tree.loader.builder.ActualTrainingTreeBuilder;
import ru.codingworkshop.gymm.data.tree.node.ActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;

/**
 * Created by Радик on 01.10.2017 as part of the Gymm project.
 */

public class TreeBuilders {
    public static ProgramTrainingTree buildProgramTrainingTree(int exercisesCount) {
        ProgramTrainingTree tree = new MutableProgramTrainingTree();
        tree.setParent(Models.createProgramTraining(1L, "foo"));
        tree.setChildren(Lists.transform(Models.createProgramExercises(exercisesCount), pe -> {
            ProgramExerciseNode node = new MutableProgramExerciseNode(pe);

            long exerciseId = pe.getExerciseId();
            node.setExercise(Models.createExercise(exerciseId, "exercise" + exerciseId));

            node.setChildren(Models.createProgramSets(pe.getId(), 2));

            return node;
        }));

        return tree;
    }

    public static ActualTrainingTree buildFullPopulatedTree(int exercisesCount) {
        ActualTrainingTreeBuilder builder = new ActualTrainingTreeBuilder(new ActualTrainingTree());

        ProgramTrainingTree programTrainingTree = buildProgramTrainingTree(exercisesCount);
        builder.setProgramTrainingTree(programTrainingTree);

        ActualTraining actualTraining = Models.createActualTraining(11L, 1L);
        builder.setParent(actualTraining);
        List<ActualSet> actualSets = new ArrayList<>();
        AtomicLong i = new AtomicLong(13L);
        builder.setChildren(new ArrayList<>(Lists.transform(
                programTrainingTree.getChildren(),
                pe -> {
                    ActualExercise actualExercise = Models.createActualExercise(
                            pe.getId() + 10, pe.getExercise().getName(), actualTraining.getId(), pe.getId());
                    for (ProgramSet s : pe.getChildren()) {
                        ActualSet actualSet = Models.createActualSet(i.getAndIncrement(), actualExercise.getId(), s.getReps());
                        actualSet.setWeight(s.getReps() + 0.5);
                        actualSets.add(actualSet);
                    }
                    return actualExercise;
                })));
        builder.setGrandchildren(actualSets);

        return (ActualTrainingTree) builder.build();
    }

    public static ActualTrainingTree buildHalfPopulatedTree(int exercisesCount) {
        Preconditions.checkArgument(exercisesCount > 1, "Exercises count must be more than 1");
        // since the method is for testing purposes
        // a full tree taken and removed last actual sets
        ActualTrainingTree tree = buildFullPopulatedTree(exercisesCount);

        List<ActualExerciseNode> exercises = tree.getChildren();
        int half = exercisesCount >> 1;
        List<ActualSet> children = exercises.get(half).getChildren();
        children.removeAll(children.subList(children.size() / 2, children.size()));

        for (int i = half + 1; i < exercisesCount; i++) {
            exercises.get(i).getChildren().clear();
        }

        return tree;
    }

    public static ActualTrainingTree buildTreeWithoutActuals(int exercisesCount) {
        ActualTrainingTreeBuilder builder = new ActualTrainingTreeBuilder(new ActualTrainingTree());

        builder.setProgramTrainingTree(buildProgramTrainingTree(exercisesCount));

        return (ActualTrainingTree) builder.build();
    }
}
