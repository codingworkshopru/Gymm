package ru.codingworkshop.gymm.data.tree.loader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.node.AbstractProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.BaseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */
final class ProgramTrainingTreeAdapter extends BaseNode<ProgramTraining, ProgramExercise> {
    private AbstractProgramTrainingTree tree;
    private Multimap<Long, ProgramSet> programSetsMultimap;
    private Map<Long, Exercise> exercisesMap;
    private List<ProgramExercise> programExercises;

    ProgramTrainingTreeAdapter(AbstractProgramTrainingTree tree) {
        super(null);
        this.tree = tree;
    }

    @Override
    public void setParent(ProgramTraining parent) {
        tree.setParent(parent);
    }

    @Override
    public void setChildren(List<ProgramExercise> children) {
        programExercises = children;
    }

    void setProgramSets(List<ProgramSet> programSets) {
        this.programSetsMultimap = Multimaps.index(programSets, ProgramSet::getProgramExerciseId);
    }

    void setExercises(List<Exercise> exercises) {
        this.exercisesMap = Maps.uniqueIndex(exercises, Exercise::getId);
    }

    void build() {
        List<ProgramExerciseNode> nodes = Lists.newArrayListWithCapacity(programExercises.size());
        for (ProgramExercise programExercise : programExercises) {
            long programExerciseId = programExercise.getId();
            Collection<ProgramSet> programSets = programSetsMultimap.get(programExerciseId);
            ArrayList<ProgramSet> programSetList = new ArrayList<>(programSets);

            long exerciseId = programExercise.getExerciseId();
            Exercise exercise = exercisesMap.get(exerciseId);

            ProgramExerciseNode node = tree.createChildNode(programExercise);
            node.setExercise(exercise);
            node.setChildren(programSetList);

            nodes.add(node);
        }
        tree.setChildren(nodes);
    }
}
