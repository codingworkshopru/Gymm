package ru.codingworkshop.gymm.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

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
}
