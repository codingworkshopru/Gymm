package ru.codingworkshop.gymm.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.WorkerThread;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.db.dao.ExerciseDao;
import ru.codingworkshop.gymm.db.dao.MuscleGroupDao;

/**
 * Created by Радик on 31.05.2017.
 */

@Singleton
public final class ExercisesRepository {
    private final ExerciseDao exerciseDao;
    private final SecondaryMuscleGroupsHelper helper;
    private final Executor executor;

    @Inject
    ExercisesRepository(ExerciseDao exerciseDao, MuscleGroupDao muscleGroupDao, Executor executor) {
        this.exerciseDao = exerciseDao;
        this.executor = executor;
        helper = new SecondaryMuscleGroupsHelper(exerciseDao, muscleGroupDao);
    }

    // createTraining

    public void create(Exercise exercise, List<MuscleGroup> secondaryMuscleGroups) {
        executor.execute(() -> {
            exerciseDao.insertExercise(exercise);
            Exercise exerciseWithId = exerciseDao.getExerciseByName(exercise.getName());
            helper.updateLinks(exerciseWithId, secondaryMuscleGroups);
        });
    }

    @WorkerThread
    // called from initializer
    public void createWithSecondaryMuscleGroups(List<Exercise> exercises) {
        helper.createExercises(exercises);
    }

    // read

    public boolean isEmpty() {
        return exerciseDao.getExercisesCount() == 0;
    }

    public LiveData<List<Exercise>> getExercisesForMuscleGroup(long id) {
        return exerciseDao.getExercisesForPrimaryMuscleGroup(id);
    }

    public LiveData<Exercise> getExerciseById(long id) {
        return exerciseDao.getExerciseById(id);
    }

    public LiveData<List<MuscleGroup>> getSecondaryMuscleGroupsForExercise(long id) {
        return exerciseDao.getSecondaryMuscleGroupsForExercise(id);
    }

    // update

    public void update(Exercise exercise, List<MuscleGroup> secondaryMuscleGroups) {
        executor.execute(() -> {
            exerciseDao.updateExercise(exercise);
            helper.updateLinks(exercise, secondaryMuscleGroups);
        });
    }

    // delete

    public void delete(Exercise exercise) {
        executor.execute(() -> exerciseDao.deleteExercise(exercise));
    }
}
