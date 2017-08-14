package ru.codingworkshop.gymm.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.Collection;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;

/**
 * Created by Радик on 04.06.2017.
 */

@Dao
public interface ActualTrainingDao {
    @Query("select * from ActualTraining where id = :actualTrainingId")
    LiveData<ActualTraining> getActualTrainingById(long actualTrainingId);

    @Insert
    Long insertActualTraining(ActualTraining actualTraining);

    @Insert
    List<Long> insertActualExercises(Collection<ActualExercise> actualExercises);

    @Insert
    Long insertActualSet(ActualSet actualSet);

    @Query("select * " +
            "from ActualExercise " +
            "where actualTrainingId = :actualTrainingId")
    LiveData<List<ActualExercise>> getActualExercisesForActualTraining(long actualTrainingId);

    @Query("select aset.* " +
            "from ActualSet aset " +
            "join ActualExercise ae on ae.id = aset.actualExerciseId " +
            "where ae.actualTrainingId = :actualTrainingId " +
            "order by ae.id, aset.id")
    LiveData<List<ActualSet>> getActualSetsForActualTraining(long actualTrainingId);
}
