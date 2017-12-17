package ru.codingworkshop.gymm.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.Collection;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;
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

    @Query("select * from MuscleGroup where isAnterior = :isAnterior order by name")
    LiveData<List<MuscleGroup>> getMuscleGroups(boolean isAnterior);

    @Query("select mg.* from MuscleGroup mg join Exercise e on e.primaryMuscleGroupId = mg.id where e.name = :exerciseName")
    MuscleGroup getMuscleGroupByExerciseNameSync(String exerciseName);

    @Query("select count(*) from MuscleGroup")
    int getMuscleGroupsCount();

    @Insert(onConflict = FAIL)
    void insertMuscleGroups(Collection<MuscleGroup> muscleGroups);

    @Query("select * from MuscleGroup where id = :muscleGroupId")
    LiveData<MuscleGroup> getMuscleGroupById(long muscleGroupId);

    @Query("select mg.* from MuscleGroup as mg " +
            "join SecondaryMuscleGroupLink as l on l.muscleGroupId = mg.id " +
            "where l.exerciseId = :exerciseId " +
            "order by name")
    LiveData<List<MuscleGroup>> getSecondaryMuscleGroupsForExercise(long exerciseId);

    @Query("select mg.* from MuscleGroup as mg " +
            "join SecondaryMuscleGroupLink as l on l.muscleGroupId = mg.id " +
            "where l.exerciseId = :exerciseId " +
            "order by name")
    Maybe<List<MuscleGroup>> getSecondaryMuscleGroupsForExerciseRx(long exerciseId);

    @Query("select mg.* from MuscleGroup as mg " +
            "join Exercise e on e.primaryMuscleGroupId = mg.id " +
            "where e.id = :exerciseId")
    LiveData<MuscleGroup> getPrimaryMuscleGroupForExercise(long exerciseId);

    @Query("select mg.* from MuscleGroup as mg " +
            "join Exercise e on e.primaryMuscleGroupId = mg.id " +
            "where e.id = :exerciseId")
    Single<MuscleGroup> getPrimaryMuscleGroupForExerciseRx(long exerciseId);
}