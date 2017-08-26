package ru.codingworkshop.gymm.ui.actual;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.tree.loader.ActualTrainingTreeLoader;
import ru.codingworkshop.gymm.data.tree.loader.ProgramTrainingTreeLoader;
import ru.codingworkshop.gymm.data.tree.loader.builder.ActualTrainingTreeBuilder;
import ru.codingworkshop.gymm.data.tree.loader.datasource.ActualTrainingDataSource;
import ru.codingworkshop.gymm.data.tree.loader.datasource.ProgramTrainingDataSource;
import ru.codingworkshop.gymm.data.tree.node.ActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.repository.ActualTrainingRepository;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Радик on 25.08.2017 as part of the Gymm project.
 */

public class ActualTrainingViewModel extends ViewModel {
    private ActualTrainingRepository actualTrainingRepository;
    private ProgramTrainingRepository programTrainingRepository;
    private ExercisesRepository exercisesRepository;

    private ActualTrainingTree tree;
    private ProgramTrainingTree programTrainingTree;

    @Inject
    public ActualTrainingViewModel(ActualTrainingRepository actualTrainingRepository,
                                   ProgramTrainingRepository programTrainingRepository,
                                   ExercisesRepository exercisesRepository
    ) {
        this.actualTrainingRepository = actualTrainingRepository;
        this.programTrainingRepository = programTrainingRepository;
        this.exercisesRepository = exercisesRepository;
    }

    public ActualTrainingTree getActualTrainingTree() {
        return tree;
    }

    public LiveData<Boolean> startTraining(final long programTrainingId) {
        return getLiveResult(() -> buildActualTrainingTree(programTrainingId));
    }

    @NonNull
    private LiveData<Boolean> buildActualTrainingTree(final long programTrainingId) {
        ActualTraining actualTraining = new ActualTraining(programTrainingId);
        actualTrainingRepository.insertActualTraining(actualTraining);

        ActualTrainingTreeBuilder builder =
                (ActualTrainingTreeBuilder) new ActualTrainingTreeBuilder(tree).setParent(actualTraining);

        return Transformations.map(loadProgramTrainingTree(programTrainingId), input -> {
            builder.setProgramTrainingTree(programTrainingTree).build();
            return true;
        });
    }

    public LiveData<Boolean> loadTraining(final long actualTrainingId) {
        return getLiveResult(() -> loadActualTrainingTree(actualTrainingId));
    }

    @NonNull
    private LiveData<Boolean> loadActualTrainingTree(final long actualTrainingId) {
        return Transformations.switchMap(actualTrainingRepository.getActualTrainingById(actualTrainingId), input -> { // TODO: 26.08.2017 this method invokes two times, should only one
            final Long programTrainingId = Preconditions.checkNotNull(input.getProgramTrainingId());
            return Transformations.switchMap(loadProgramTrainingTree(programTrainingId), unused -> {
                ActualTrainingDataSource dataSource = new ActualTrainingDataSource(actualTrainingRepository, actualTrainingId);
                ActualTrainingTreeLoader loader = new ActualTrainingTreeLoader(getActualTrainingTreeBuilder(), dataSource);
                return loader.load();
            });
        });
    }

    @NonNull
    private ActualTrainingTreeBuilder getActualTrainingTreeBuilder() {
        return new ActualTrainingTreeBuilder(tree).setProgramTrainingTree(programTrainingTree);
    }

    private LiveData<Boolean> getLiveResult(Supplier<LiveData<Boolean>> getLiveResult) {
        LiveData<Boolean> result;

        if (tree == null) {
            tree = new ActualTrainingTree();
            result = getLiveResult.get();
        } else {
            result = LiveDataUtil.getLive(true);
        }

        return result;
    }

    private LiveData<Boolean> loadProgramTrainingTree(long programTrainingId) {
        programTrainingTree = new ImmutableProgramTrainingTree();

        ProgramTrainingDataSource dataSource = new ProgramTrainingDataSource(programTrainingRepository, exercisesRepository, programTrainingId);
        ProgramTrainingTreeLoader loader = new ProgramTrainingTreeLoader(programTrainingTree, dataSource);

        return loader.load();
    }

    public void createActualExercise(int actualExerciseNodeIndex) {
        ActualExerciseNode node = getActualExerciseNode(actualExerciseNodeIndex);
        ProgramExerciseNode programExerciseNode = node.getProgramExerciseNode();
        ActualExercise actualExercise = new ActualExercise(
                programExerciseNode.getExercise().getName(),
                tree.getParent().getId(),
                programExerciseNode.getId());
        actualTrainingRepository.insertActualExercise(actualExercise);
        node.setParent(actualExercise);
    }

    public void createActualSet(int actualExerciseNodeIndex, int reps, double weight) {
        ActualExerciseNode node = getActualExerciseNode(actualExerciseNodeIndex);

        ActualSet actualSet = new ActualSet(node.getParent().getId(), reps);
        actualSet.setWeight(weight);
        actualTrainingRepository.insertActualSet(actualSet);

        node.addChild(actualSet);
    }

    public void updateActualSet(int actualExerciseNodeIndex, int actualSetIndex, int reps, double weight) {
        ActualSet set = getActualSet(actualExerciseNodeIndex, actualSetIndex);
        set.setReps(reps);
        set.setWeight(weight);

        actualTrainingRepository.updateActualSet(set);
    }

    public void deleteActualSet(int actualExerciseNodeIndex, int actualSetIndex) {
        ActualSet set = getActualSet(actualExerciseNodeIndex, actualSetIndex);
        getActualExerciseNode(actualExerciseNodeIndex).removeChild(actualSetIndex);
        actualTrainingRepository.deleteActualSet(set);
    }

    private ActualSet getActualSet(int actualExerciseNodeIndex, int actualSetIndex) {
        return getActualExerciseNode(actualExerciseNodeIndex).getChildren().get(actualSetIndex);
    }

    private ActualExerciseNode getActualExerciseNode(int actualExerciseNodeIndex) {
        return tree.getChildren().get(actualExerciseNodeIndex);
    }
}
