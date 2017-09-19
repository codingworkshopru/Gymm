package ru.codingworkshop.gymm.ui.actual.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

import java.util.Collection;
import java.util.Date;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.tree.node.ActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.db.GymmDatabase;
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
        return getLiveResult(new ActualTrainingCreateTreeDelegate(programTrainingId));
    }

    public LiveData<Boolean> loadTraining(final long actualTrainingId) {
        return getLiveResult(new ActualTrainingLoadTreeDelegate(actualTrainingId));
    }

    public void finishTraining() {
        Collection<ActualExerciseNode> nodesWithoutSets = Collections2.filter(tree.getChildren(), node -> !node.hasChildren());
        boolean hasData = tree.getChildren().size() != nodesWithoutSets.size();
        ActualTraining actualTraining = tree.getParent();
        if (hasData) {
            actualTraining.setFinishTime(new Date());
            actualTrainingRepository.updateActualTraining(actualTraining);
            Collection<ActualExercise> emptyExercises = Collections2.transform(nodesWithoutSets, node -> node.getParent());
            if (!emptyExercises.isEmpty()) {
                actualTrainingRepository.deleteActualExercises(emptyExercises);
            }
        } else {
            actualTrainingRepository.deleteActualTraining(actualTraining);
        }
    }

    private void setUpDelegate(ActualTrainingTreeDelegate delegate) {
        delegate.setActualTrainingRepository(actualTrainingRepository);
        delegate.setProgramTrainingRepository(programTrainingRepository);
        delegate.setExercisesRepository(exercisesRepository);
    }

    private LiveData<Boolean> getLiveResult(ActualTrainingTreeDelegate delegate) {
        LiveData<Boolean> result;

        if (tree == null) {
            tree = new ActualTrainingTree();
            setUpDelegate(delegate);
            result = delegate.load(tree);
        } else {
            result = LiveDataUtil.getLive(true);
        }

        return result;
    }

    public void createActualExercise(int actualExerciseNodeIndex) {
        ActualExerciseNode node = getActualExerciseNode(actualExerciseNodeIndex);
        if (node.getParent() == null) {
            ProgramExerciseNode programExerciseNode = node.getProgramExerciseNode();
            ActualExercise actualExercise = new ActualExercise(
                    programExerciseNode.getExercise().getName(),
                    tree.getParent().getId(),
                    programExerciseNode.getId());
            actualTrainingRepository.insertActualExercise(actualExercise);
            node.setParent(actualExercise);
        }
    }

    public void createActualSet(int actualExerciseNodeIndex, @NonNull ActualSet actualSet) {
        Preconditions.checkNotNull(actualSet);

        ActualExerciseNode node = getActualExerciseNode(actualExerciseNodeIndex);
        actualSet.setActualExerciseId(node.getParent().getId());
        actualTrainingRepository.insertActualSet(actualSet);

        node.addChild(actualSet);
    }

    public void updateActualSet(int actualExerciseNodeIndex, @NonNull ActualSet actualSet) {
        Preconditions.checkArgument(GymmDatabase.isValidId(actualSet));
        ActualExerciseNode node = getActualExerciseNode(actualExerciseNodeIndex);
        ActualSet foundActualSet = Iterables.find(node.getChildren(), s -> s.getId() == actualSet.getId());
        Preconditions.checkState(foundActualSet != null);
        if (actualSet != foundActualSet) {
            int index = node.getChildren().indexOf(foundActualSet);
            node.getChildren().set(index, actualSet);
        }
        actualTrainingRepository.updateActualSet(actualSet);
    }

    private ActualExerciseNode getActualExerciseNode(int actualExerciseNodeIndex) {
        return tree.getChildren().get(actualExerciseNodeIndex);
    }
}
