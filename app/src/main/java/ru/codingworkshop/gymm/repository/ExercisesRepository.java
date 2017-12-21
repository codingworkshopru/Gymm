package ru.codingworkshop.gymm.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.WorkerThread;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.db.dao.ExerciseDao;
import ru.codingworkshop.gymm.db.dao.MuscleGroupDao;

/**
 * Created by Радик on 31.05.2017.
 */

@Singleton
public class ExercisesRepository {
    private final ExerciseDao exerciseDao;
    private final SecondaryMuscleGroupsHelper helper;

    @Inject
    ExercisesRepository(ExerciseDao exerciseDao, MuscleGroupDao muscleGroupDao) {
        this.exerciseDao = exerciseDao;
        helper = new SecondaryMuscleGroupsHelper(exerciseDao, muscleGroupDao);
    }

    @WorkerThread
    // called from initializer
    public void createWithSecondaryMuscleGroups(List<Exercise> exercises) {
        helper.createExercises(exercises);
    }

    public boolean isEmpty() {
        return exerciseDao.getExercisesCount() == 0;
    }

    public LiveData<List<Exercise>> getExercisesForMuscleGroup(long muscleGroupId) {
        return exerciseDao.getExercisesForPrimaryMuscleGroup(muscleGroupId);
    }

    public Flowable<Exercise> getExerciseById(long id) {
        return exerciseDao.getExerciseById(id);
    }

    public Flowable<List<Exercise>> getExercisesForProgramTraining(long programTrainingId) {
        return exerciseDao.getExercisesForProgramTraining(programTrainingId);
    }
}
