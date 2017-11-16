package ru.codingworkshop.gymm.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.db.GymmDatabase;
import ru.codingworkshop.gymm.db.dao.ActualTrainingDao;

import static ru.codingworkshop.gymm.db.GymmDatabase.isValidId;

/**
 * Created by Радик on 29.07.2017.
 */

@Singleton
public class ActualTrainingRepository extends BaseRepository {
    private ActualTrainingDao dao;

    @Inject
    public ActualTrainingRepository(Executor executor, ActualTrainingDao dao) {
        super(executor);
        this.dao = dao;
    }

    public LiveData<ActualTraining> getActualTrainingById(long actualTrainingId) {
        return dao.getActualTrainingById(actualTrainingId);
    }

    public void insertActualTraining(@NonNull ActualTraining actualTraining) {
        checkActualTraining(actualTraining);
        insert(actualTraining, dao::insertActualTraining);
    }

    public LiveData<Long> insertActualTrainingWithResult(ActualTraining actualTraining) {
        checkActualTraining(actualTraining);
        return insertWithResult(actualTraining, dao::insertActualTraining);
    }

    public void updateActualTraining(ActualTraining actualTraining) {
        checkActualTraining(actualTraining);
        update(actualTraining, dao::updateActualTraining);
    }

    public void deleteActualTraining(ActualTraining actualTraining) {
        delete(actualTraining, dao::deleteActualTraining);
    }

    private static void checkActualTraining(@NonNull ActualTraining actualTraining) {
        Preconditions.checkNotNull(actualTraining.getStartTime());
        Preconditions.checkArgument(actualTraining.getProgramTrainingId() != null
                && isValidId(actualTraining.getProgramTrainingId())
                && !Strings.isNullOrEmpty(actualTraining.getName())
        );
    }

    public LiveData<List<ActualExercise>> getActualExercisesForActualTraining(long actualTrainingId) {
        return dao.getActualExercisesForActualTraining(actualTrainingId);
    }

    public void insertActualExercise(ActualExercise actualExercise) {
        checkActualExercise(actualExercise);
        insert(actualExercise, dao::insertActualExercise);
    }

    public void insertActualExercises(Collection<ActualExercise> actualExercises) {
        applyToEach(actualExercises, ActualTrainingRepository::checkActualExercise);
        insert(actualExercises, dao::insertActualExercises);
    }

    public void deleteActualExercises(Collection<ActualExercise> actualExercises) {
        delete(actualExercises, dao::deleteActualExercises);
    }

    private static void checkActualExercise(@NonNull ActualExercise actualExercise) {
        Preconditions.checkArgument(actualExercise.getProgramExerciseId() != null && isValidId(actualExercise.getProgramExerciseId()));
        Preconditions.checkArgument(isValidId(actualExercise.getActualTrainingId()));
    }

    public LiveData<List<ActualSet>> getActualSetsForActualTraining(long actualTrainingId) {
        return dao.getActualSetsForActualTraining(actualTrainingId);
    }

    public void insertActualSet(ActualSet actualSet) {
        checkActualSet(actualSet);
        insert(actualSet, dao::insertActualSet);
    }

    public LiveData<Long> insertActualSetWithResult(ActualSet actualSet) {
        checkActualSet(actualSet);
        return insertWithResult(actualSet, dao::insertActualSet);
    }

    public void updateActualSet(ActualSet actualSet) {
        checkActualSet(actualSet);
        Preconditions.checkArgument(GymmDatabase.isValidId(actualSet));
        update(actualSet, dao::updateActualSet);
    }

    public void deleteActualSet(ActualSet actualSet) {
        delete(actualSet, dao::deleteActualSet);
    }

    private static void checkActualSet(@NonNull ActualSet actualSet) {
        final Double weight = actualSet.getWeight();
        if (weight != null && weight == 0.0) {
            actualSet.setWeight(null);
        }
        Preconditions.checkArgument(actualSet.getReps() != 0);
        Preconditions.checkArgument(isValidId(actualSet.getActualExerciseId()));
    }
}
