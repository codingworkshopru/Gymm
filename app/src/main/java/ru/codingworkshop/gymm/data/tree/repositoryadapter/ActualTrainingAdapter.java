package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.arch.lifecycle.LiveData;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.repository.ActualTrainingRepository;

/**
 * Created by Radik on 13.11.2017.
 */

public class ActualTrainingAdapter implements ParentAdapter<ActualTraining>, ChildrenAdapter<ActualExercise>, GrandchildrenAdapter<ActualSet> {
    private ActualTrainingRepository repository;

    @Inject
    public ActualTrainingAdapter(ActualTrainingRepository repository) {
        this.repository = repository;
    }

    @Override
    public LiveData<ActualTraining> getParent(long id) {
        return repository.getActualTrainingById(id);
    }

    @Override
    public void updateParent(ActualTraining item) {
        repository.updateActualTraining(item);
    }

    @Override
    public LiveData<Long> insertParent(ActualTraining item) {
        repository.insertActualTraining(item);
        return null;
    }

    @Override
    public void deleteParent(ActualTraining item) {
        repository.deleteActualTraining(item);
    }

    @Override
    public LiveData<List<ActualExercise>> getChildren(long id) {
        return repository.getActualExercisesForActualTraining(id);
    }

    @Override
    public LiveData<List<Long>> insertChildren(Collection<ActualExercise> children) {
        repository.insertActualExercises(children);
        return null;
    }

    @Override
    public void updateChildren(Collection<ActualExercise> children) {
    }

    @Override
    public void deleteChildren(Collection<ActualExercise> children) {
        repository.deleteActualExercises(children);
    }

    @Override
    public LiveData<List<ActualSet>> getGrandchildren(long id) {
        return repository.getActualSetsForActualTraining(id);
    }

    @Override
    public void insertGrandchildren(Collection<ActualSet> grandchildren) {

    }

    @Override
    public void updateGrandchildren(Collection<ActualSet> grandchildren) {

    }

    @Override
    public void deleteGrandchildren(Collection<ActualSet> grandchildren) {

    }
}
