package ru.codingworkshop.gymm.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.db.dao.MuscleGroupDao;

/**
 * Created by Радик on 24.05.2017.
 */

@Singleton
public final class MuscleGroupsRepository {
    private MuscleGroupDao muscleGroupDao;

    @Inject
    MuscleGroupsRepository(MuscleGroupDao muscleGroupDao) {
        this.muscleGroupDao = muscleGroupDao;
    }

    public boolean isEmpty() {
        return muscleGroupDao.getMuscleGroupsCount() == 0;
    }

    public LiveData<List<MuscleGroup>> getMuscleGroups() {
        return muscleGroupDao.getAllMuscleGroups();
    }

    public void insertMuscleGroups(List<MuscleGroup> muscleGroups) {
        muscleGroupDao.insertMuscleGroups(muscleGroups);
    }
}
