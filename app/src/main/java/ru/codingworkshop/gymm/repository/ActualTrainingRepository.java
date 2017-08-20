package ru.codingworkshop.gymm.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.db.dao.ActualTrainingDao;

import static ru.codingworkshop.gymm.db.GymmDatabase.isValidId;

/**
 * Created by Радик on 29.07.2017.
 */

public class ActualTrainingRepository extends BaseRepository {
    private ActualTrainingDao dao;

    @Inject
    public ActualTrainingRepository(Executor executor, ActualTrainingDao dao) {
        super(executor);
        this.dao = dao;
    }

    @VisibleForTesting
    ActualTrainingRepository(Executor executor, ATask aTask, ActualTrainingDao dao) {
        super(executor, aTask);
        this.dao = dao;
    }

    public LiveData<ActualTraining> getActualTrainingById(long actualTrainingId) {
        return dao.getActualTrainingById(actualTrainingId);
    }

    public void insertActualTraining(@NonNull ActualTraining actualTraining) {
        insert(actualTraining, dao::insertActualTraining, ActualTrainingRepository::checkActualTraining);
    }

    private static void checkActualTraining(@NonNull ActualTraining actualTraining) {
        Preconditions.checkNotNull(actualTraining.getStartTime());
        Preconditions.checkArgument(actualTraining.getProgramTrainingId() != null && isValidId(actualTraining.getProgramTrainingId()));
    }

    public LiveData<List<ActualExercise>> getActualExercisesForActualTraining(long actualTrainingId) {
        return dao.getActualExercisesForActualTraining(actualTrainingId);
    }

    public void insertActualExercises(Collection<ActualExercise> actualExercises) {
        insert(actualExercises, dao::insertActualExercises, ActualTrainingRepository::checkActualExercise);
    }

    private static void checkActualExercise(@NonNull ActualExercise actualExercise) {
        Preconditions.checkArgument(actualExercise.getProgramExerciseId() != null && isValidId(actualExercise.getProgramExerciseId()));
        Preconditions.checkArgument(isValidId(actualExercise.getActualTrainingId()));
    }

    public LiveData<List<ActualSet>> getActualSetsForActualTraining(long actualTrainingId) {
        return dao.getActualSetsForActualTraining(actualTrainingId);
    }

    public void insertActualSet(ActualSet actualSet) {
        insert(actualSet, dao::insertActualSet, ActualTrainingRepository::checkActualSet);
    }

    private static void checkActualSet(@NonNull ActualSet actualSet) {
        Preconditions.checkArgument(isValidId(actualSet.getActualExerciseId()));
    }
}
