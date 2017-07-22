package ru.codingworkshop.gymm.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.data.entity.SecondaryMuscleGroupLink;

import static android.arch.persistence.room.OnConflictStrategy.FAIL;

/**
 * Created by Радик on 22.05.2017.
 */

@Dao
public interface ExerciseDao {
    @Query("select * from exercise")
    LiveData<List<Exercise>> getAllExercises();

    @Query("select * from exercise")
    List<Exercise> getAllExercisesSync();

    @Query("select * from exercise where primaryMuscleGroupId = :id")
    LiveData<List<Exercise>> getExercisesForPrimaryMuscleGroup(long id);

    @Query("select * from exercise where id = :id")
    LiveData<Exercise> getExerciseById(long id);

    @Query("select * from exercise where name = :name")
    Exercise getExerciseByName(String name);

    @Query("select * from exercise where id = :id")
    Exercise getExerciseByIdSync(long id);

    @Query("select count(*) from exercise")
    int getExercisesCount();

    @Query(
            "select e.* from exercise as e " +
                    "join SecondaryMuscleGroupLink as l on l.exerciseId = e.id " +
                    "where l.muscleGroupId = :muscleGroupId " +
                    "order by e.name"
    )
    LiveData<List<Exercise>> getExercisesForSecondaryMuscleGroup(long muscleGroupId);

    @Query(
            "select mg.* from MuscleGroup as mg " +
                    "join SecondaryMuscleGroupLink as l on l.muscleGroupId = mg.id " +
                    "where l.exerciseId = :exerciseId " +
                    "order by mg.name"
    )
    LiveData<List<MuscleGroup>> getSecondaryMuscleGroupsForExercise(long exerciseId);

    @Query("select * from SecondaryMuscleGroupLink where exerciseId = :exerciseId ")
    List<SecondaryMuscleGroupLink> getSecondaryMuscleGroupLinkSync(long exerciseId);

    @Insert(onConflict = FAIL)
    void insertExercises(List<Exercise> entities);

    @Insert(onConflict = FAIL)
    void insertExercise(Exercise exercise);

    @Update(onConflict = FAIL)
    void updateExercise(Exercise exercise);

    @Delete
    void deleteExercise(Exercise exercise);

    @Insert(onConflict = FAIL)
    void createLinks(List<SecondaryMuscleGroupLink> links);

    @Delete
    void deleteLinks(List<SecondaryMuscleGroupLink> link);
}
