package ru.codingworkshop.gymm.repository;

import android.arch.lifecycle.LiveData;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.db.dao.MuscleGroupDao;

/**
 * Created by Радик on 24.05.2017.
 */

@Singleton
public class MuscleGroupsRepository {
    private MuscleGroupDao dao;

    @Inject
    MuscleGroupsRepository(MuscleGroupDao muscleGroupDao) {
        this.dao = muscleGroupDao;
    }

    public boolean isEmpty() {
        return dao.getMuscleGroupsCount() == 0;
    }

    public LiveData<List<MuscleGroup>> getMuscleGroups() {
        return dao.getAllMuscleGroups();
    }

    public LiveData<List<MuscleGroup>> getMuscleGroups(boolean isAnterior) {
        return dao.getMuscleGroups(isAnterior);
    }

    public void insertMuscleGroups(Collection<MuscleGroup> muscleGroups) {
        dao.insertMuscleGroups(muscleGroups);
    }

    public LiveData<MuscleGroup> getMuscleGroupById(long muscleGroupId) {
        return dao.getMuscleGroupById(muscleGroupId);
    }

    public Flowable<List<MuscleGroup>> getSecondaryMuscleGroupsForExercise(long exerciseId) {
        return dao.getSecondaryMuscleGroupsForExercise(exerciseId);
    }

    public Flowable<MuscleGroup> getPrimaryMuscleGroupForExercise(long exerciseId) {
        return dao.getPrimaryMuscleGroupForExercise(exerciseId);
    }
}
