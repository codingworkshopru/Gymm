package ru.codingworkshop.gymm.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.entity.ExercisePlotTuple;
import ru.codingworkshop.gymm.db.Converters;

/**
 * Created by Радик on 04.06.2017.
 */

@Dao
public interface ActualTrainingDao {
    @Query("select * from ActualTraining where id = :actualTrainingId")
    LiveData<ActualTraining> getActualTrainingById(long actualTrainingId);

    @Insert
    Long insertActualTraining(ActualTraining actualTraining);

    @Update
    int updateActualTraining(ActualTraining training);

    @Delete
    int deleteActualTraining(ActualTraining training);

    @Insert
    List<Long> insertActualExercises(Collection<ActualExercise> actualExercises);

    @Insert
    Long insertActualExercise(ActualExercise actualExercise);

    @Delete
    int deleteActualExercises(Collection<ActualExercise> actualExercises);

    @Insert
    Long insertActualSet(ActualSet actualSet);

    @Insert
    void insertActualSets(Collection<ActualSet> actualSets);

    @Update
    int updateActualSet(ActualSet actualSet);

    @Delete
    int deleteActualSet(ActualSet actualSet);

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

    @Query("select distinct exerciseName from ActualExercise order by exerciseName")
    LiveData<List<String>> getActualExerciseNames();

    /**
     * Returns LiveData with statistics on particular exercise for the period of time till today.
     * @param exerciseName - get statistics for this exercise
     * @param startDate - pass null if need data for all the time
     * @return {@link ExercisePlotTuple}
     */
    @TypeConverters(Converters.class)
    @Query("select startTime as trainingTime, reps, weight " +
            "from ActualExercise e " +
            "join ActualTraining t on t.id = e.actualTrainingId " +
            "join ActualSet s on s.actualExerciseId = e.id " +
            "where e.exerciseName = :exerciseName and (startTime > coalesce(:startDate, 0)) " +
            "order by startTime")
    List<ExercisePlotTuple> getStatisticsForExerciseSync(String exerciseName, @Nullable Date startDate);
}
