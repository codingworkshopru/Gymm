package ru.codingworkshop.gymm.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.entity.ExercisePlotTuple;
import ru.codingworkshop.gymm.db.GymmDatabase;
import ru.codingworkshop.gymm.db.dao.ActualTrainingDao;

import static ru.codingworkshop.gymm.db.GymmDatabase.isValidId;

/**
 * Created by Радик on 29.07.2017.
 */

@Singleton
public class ActualTrainingRepository {
    private ActualTrainingDao dao;
    private InsertDelegate insertDelegate;

    @Inject
    public ActualTrainingRepository(ActualTrainingDao dao, InsertDelegate insertDelegate) {
        this.dao = dao;
        this.insertDelegate = insertDelegate;
    }

    public ActualTraining getActualTrainingById(long actualTrainingId) {
        return dao.getActualTrainingById(actualTrainingId);
    }

    public long insertActualTraining(@NonNull ActualTraining actualTraining) {
        checkActualTraining(actualTraining);
        return insertDelegate.insert(actualTraining, dao::insertActualTraining);
    }

    public void updateActualTraining(ActualTraining actualTraining) {
        checkActualTraining(actualTraining);
        dao.updateActualTraining(actualTraining);
    }

    public void deleteActualTraining(ActualTraining actualTraining) {
        dao.deleteActualTraining(actualTraining);
    }

    private static void checkActualTraining(@NonNull ActualTraining actualTraining) {
        Preconditions.checkNotNull(actualTraining.getStartTime());
        Preconditions.checkArgument(actualTraining.getProgramTrainingId() != null
                && isValidId(actualTraining.getProgramTrainingId())
                && !Strings.isNullOrEmpty(actualTraining.getName())
        );
    }

    public List<ActualExercise> getActualExercisesForActualTraining(long actualTrainingId) {
        return dao.getActualExercisesForActualTraining(actualTrainingId);
    }

    public long insertActualExercise(ActualExercise actualExercise) {
        checkActualExercise(actualExercise);
        return insertDelegate.insert(actualExercise, dao::insertActualExercise);
    }

    public List<Long> insertActualExercises(Collection<ActualExercise> actualExercises) {
        for (ActualExercise e : actualExercises) {
            checkActualExercise(e);
        }
        return insertDelegate.insert(actualExercises, dao::insertActualExercises);
    }

    public void deleteActualExercises(Collection<ActualExercise> actualExercises) {
        dao.deleteActualExercises(actualExercises);
    }

    private static void checkActualExercise(@NonNull ActualExercise actualExercise) {
        Preconditions.checkArgument(actualExercise.getProgramExerciseId() != null && isValidId(actualExercise.getProgramExerciseId()));
        Preconditions.checkArgument(isValidId(actualExercise.getActualTrainingId()));
    }

    public List<ActualSet> getActualSetsForActualTraining(long actualTrainingId) {
        return dao.getActualSetsForActualTraining(actualTrainingId);
    }

    public void insertActualSet(ActualSet actualSet) {
        checkActualSet(actualSet);
        dao.insertActualSet(actualSet);
    }

    public LiveData<Long> insertActualSetWithResult(ActualSet actualSet) {
        checkActualSet(actualSet);
        return LiveDataReactiveStreams.fromPublisher(
                Flowable.just(
                        insertDelegate.insert(actualSet, dao::insertActualSet)));
    }

    public void updateActualSet(ActualSet actualSet) {
        checkActualSet(actualSet);
        Preconditions.checkArgument(GymmDatabase.isValidId(actualSet));
        dao.updateActualSet(actualSet);
    }

    public void deleteActualSet(ActualSet actualSet) {
        dao.deleteActualSet(actualSet);
    }

    private static void checkActualSet(@NonNull ActualSet actualSet) {
        final Double weight = actualSet.getWeight();
        if (weight != null && weight == 0.0) {
            actualSet.setWeight(null);
        }
        Preconditions.checkArgument(actualSet.getReps() != 0);
        Preconditions.checkArgument(isValidId(actualSet.getActualExerciseId()));
    }


    public LiveData<List<String>> getActualExerciseNames() {
        return dao.getActualExerciseNames();
    }

    public List<ExercisePlotTuple> getStatisticsForExercise(@NonNull String exerciseName, @Nullable Date startDate) {
        return dao.getStatisticsForExercise(exerciseName, startDate);
    }

    public LiveData<List<ActualTraining>> getActualTrainings() {
        return dao.getActualTrainings();
    }
}
