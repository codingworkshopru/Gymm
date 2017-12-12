package ru.codingworkshop.gymm.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.SecondaryMuscleGroupLink;

import static android.arch.persistence.room.OnConflictStrategy.FAIL;

/**
 * Created by Радик on 22.05.2017.
 */

@Dao
public interface ExerciseDao {
    @Query("select * from exercise")
    LiveData<List<Exercise>> getAllExercises();

    @Query("select * from exercise where primaryMuscleGroupId = :id")
    LiveData<List<Exercise>> getExercisesForPrimaryMuscleGroup(long id);

    @Query("select * from exercise where id = :id")
    LiveData<Exercise> getExerciseById(long id);

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
            "select distinct e.* from Exercise as e " +
                    "join ProgramExercise pe on pe.exerciseId = e.id " +
                    "where pe.programTrainingId = :programTrainingId " +
                    "order by pe.sortOrder"
    )
    LiveData<List<Exercise>> getExercisesForProgramTraining(long programTrainingId);

    @Query(
            "select e.* from Exercise as e " +
                    "join ProgramExercise pe on pe.exerciseId = e.id " +
                    "where pe.id = :programExerciseId"
    )
    LiveData<Exercise> getExerciseForProgramExercise(long programExerciseId);

    @Insert(onConflict = FAIL)
    List<Long> insertExercises(List<Exercise> entities);

    @Insert(onConflict = FAIL)
    void createLinks(List<SecondaryMuscleGroupLink> links);
}
