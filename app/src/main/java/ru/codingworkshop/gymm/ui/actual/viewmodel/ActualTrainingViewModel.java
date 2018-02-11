package ru.codingworkshop.gymm.ui.actual.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

import java.util.Collection;
import java.util.Date;

import javax.inject.Inject;

import dagger.Lazy;
import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.tree.loader.ActualTrainingEmptyTreeLoader;
import ru.codingworkshop.gymm.data.tree.loader.ActualTrainingTreeLoader;
import ru.codingworkshop.gymm.data.tree.node.ActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.BaseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.db.GymmDatabase;
import ru.codingworkshop.gymm.repository.ActualTrainingRepository;

/**
 * Created by Радик on 25.08.2017 as part of the Gymm project.
 */

public class ActualTrainingViewModel extends ViewModel {
    private ActualTrainingRepository actualTrainingRepository;

    @VisibleForTesting
    ActualTrainingTree tree;
    private Lazy<ActualTrainingTreeLoader> treeLoader;
    private Lazy<ActualTrainingEmptyTreeLoader> emptyTreeLoader;

    @Inject
    public ActualTrainingViewModel(Lazy<ActualTrainingTreeLoader> treeLoader, Lazy<ActualTrainingEmptyTreeLoader> emptyTreeLoader, ActualTrainingRepository actualTrainingRepository) {
        this.treeLoader = treeLoader;
        this.emptyTreeLoader = emptyTreeLoader;
        this.actualTrainingRepository = actualTrainingRepository;
    }

    public ActualTrainingTree getActualTrainingTree() {
        return tree;
    }

    public LiveData<ActualTrainingTree> startTraining(final long programTrainingId) {
        if (tree == null) {
            tree = new ActualTrainingTree();
            return LiveDataReactiveStreams.fromPublisher(emptyTreeLoader.get()
                    .loadById(tree, programTrainingId)
                    .map(t -> {
                        actualTrainingRepository.insertActualTraining(tree.getParent());
                        return t;
                    }));
        } else {
            return LiveDataUtil.getLive(tree);
        }
    }

    public LiveData<ActualTrainingTree> loadTraining(final long actualTrainingId) {
        if (tree == null) {
            tree = new ActualTrainingTree();
            ActualTrainingTreeLoader loader = treeLoader.get();
            return LiveDataReactiveStreams.fromPublisher(loader.loadById(tree, actualTrainingId));
        } else {
            return LiveDataUtil.getLive(tree);
        }
    }

    public void finishTraining() {
        Collection<ActualExerciseNode> nodesWithoutSets = Collections2.filter(tree.getChildren(), node -> !node.hasChildren());
        boolean hasData = tree.getChildren().size() != nodesWithoutSets.size();
        ActualTraining actualTraining = tree.getParent();
        if (hasData) {
            actualTraining.setFinishTime(new Date());
            actualTrainingRepository.updateActualTraining(actualTraining);
            Collection<ActualExercise> emptyExercises = Collections2.transform(nodesWithoutSets, BaseNode::getParent);
            if (!emptyExercises.isEmpty()) {
                actualTrainingRepository.deleteActualExercises(emptyExercises);
            }
        } else {
            actualTrainingRepository.deleteActualTraining(actualTraining);
        }
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

    public LiveData<Long> createActualSet(int actualExerciseNodeIndex, @NonNull ActualSet actualSet) {
        Preconditions.checkNotNull(actualSet);

        ActualExerciseNode node = getActualExerciseNode(actualExerciseNodeIndex);
        actualSet.setActualExerciseId(node.getParent().getId());

        node.addChild(actualSet);
        return actualTrainingRepository.insertActualSetWithResult(actualSet);
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
