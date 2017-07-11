package ru.codingworkshop.gymm.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.ExerciseEntity;
import ru.codingworkshop.gymm.data.entity.MuscleGroupEntity;
import ru.codingworkshop.gymm.data.entity.SecondaryMuscleGroupLinkEntity;

import static android.arch.persistence.room.OnConflictStrategy.FAIL;

/**
 * Created by Радик on 22.05.2017.
 */

@Dao
public interface ExerciseDao {
    @Query("select * from exercise")
    LiveData<List<ExerciseEntity>> getAllExercises();

    @Query("select * from exercise")
    List<ExerciseEntity> getAllExercisesSync();

    @Query("select * from exercise where primaryMuscleGroupId = :id")
    LiveData<List<ExerciseEntity>> getExercisesForPrimaryMuscleGroup(long id);

    @Query("select * from exercise where id = :id")
    LiveData<ExerciseEntity> getExerciseById(long id);

    @Query("select * from exercise where name = :name")
    ExerciseEntity getExerciseByName(String name);

    @Query("select * from exercise where id = :id")
    ExerciseEntity getExerciseByIdSync(long id);

    @Query("select count(*) from exercise")
    int getExercisesCount();

    @Query(
            "select e.* from exercise as e " +
                    "join SecondaryMuscleGroupLink as l on l.exerciseId = e.id " +
                    "where l.muscleGroupId = :muscleGroupId " +
                    "order by e.name"
    )
    LiveData<List<ExerciseEntity>> getExercisesForSecondaryMuscleGroup(long muscleGroupId);

    @Query(
            "select mg.* from MuscleGroup as mg " +
                    "join SecondaryMuscleGroupLink as l on l.muscleGroupId = mg.id " +
                    "where l.exerciseId = :exerciseId " +
                    "order by mg.name"
    )
    LiveData<List<MuscleGroupEntity>> getSecondaryMuscleGroupsForExercise(long exerciseId);

    @Query("select * from SecondaryMuscleGroupLink where exerciseId = :exerciseId ")
    List<SecondaryMuscleGroupLinkEntity> getSecondaryMuscleGroupLinkSync(long exerciseId);

    @Insert(onConflict = FAIL)
    void insertExercises(List<ExerciseEntity> entities);

    @Insert(onConflict = FAIL)
    void insertExercise(ExerciseEntity exercise);

    @Update(onConflict = FAIL)
    void updateExercise(ExerciseEntity exercise);

    @Delete
    void deleteExercise(ExerciseEntity exercise);

    @Insert(onConflict = FAIL)
    void createLinks(List<SecondaryMuscleGroupLinkEntity> links);

    @Delete
    void deleteLinks(List<SecondaryMuscleGroupLinkEntity> link);
}
