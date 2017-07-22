package ru.codingworkshop.gymm.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.MuscleGroup;

import static android.arch.persistence.room.OnConflictStrategy.FAIL;

/**
 * Created by Радик on 22.05.2017.
 */

@Dao
public interface MuscleGroupDao {
    @Query("select * from MuscleGroup order by name")
    LiveData<List<MuscleGroup>> getAllMuscleGroups();

    @Query("select * from MuscleGroup order by name")
    List<MuscleGroup> getAllMuscleGroupsSync();

    @Query("select count(*) from MuscleGroup")
    int getMuscleGroupsCount();

    @Query("select * from MuscleGroup where id = :id")
    LiveData<MuscleGroup> getMuscleGroupById(long id);

    @Insert(onConflict = FAIL)
    void insertMuscleGroups(List<MuscleGroup> muscleGroups);
}