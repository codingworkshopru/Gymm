package ru.codingworkshop.gymm.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.common.base.Preconditions;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.db.dao.ActualTrainingDao;

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

    public LiveData<Long> insertActualTraining(@NonNull ActualTraining actualTraining) {
        return insert(actualTraining, dao::insertActualTraining, ActualTrainingRepository::checkActualTraining);
    }

    private static void checkActualTraining(@NonNull ActualTraining actualTraining) {
        Preconditions.checkNotNull(actualTraining.getStartTime());
        Preconditions.checkNotNull(actualTraining.getProgramTrainingId());
    }
}
