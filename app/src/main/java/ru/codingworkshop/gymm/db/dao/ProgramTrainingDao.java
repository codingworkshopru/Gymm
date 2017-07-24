package ru.codingworkshop.gymm.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Collection;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;

import static android.arch.persistence.room.OnConflictStrategy.FAIL;

/**
 * Created by Радик on 04.06.2017.
 */

@Dao
public interface ProgramTrainingDao {
    @Query("select * from ProgramTraining")
    LiveData<List<ProgramTraining>> getProgramTrainings();

    @Query("select * from ProgramTraining where id = :id")
    LiveData<ProgramTraining> getProgramTrainingById(long id);

    @Query("select * from ProgramTraining where drafting = 1 limit 1")
    LiveData<ProgramTraining> getDraftingProgramTraining();

    @Insert(onConflict = FAIL)
    long insertProgramTraining(ProgramTraining programTraining);

    @Delete
    int deleteProgramTraining(ProgramTraining programTraining);

    @Update(onConflict = FAIL)
    int updateProgramTraining(ProgramTraining programTraining);

    @Query("select * " +
            "from ProgramExercise " +
            "where programTrainingId = :programTrainingId " +
            "order by sortOrder")
    LiveData<List<ProgramExercise>> getProgramExercisesForTraining(long programTrainingId);

    @Query("select * from ProgramExercise where id = :id")
    LiveData<ProgramExercise> getProgramExerciseById(long id);

    @Query("select * from ProgramExercise where drafting = 1 and programTrainingId = :programTrainingId limit 1")
    LiveData<ProgramExercise> getDraftingProgramExercise(long programTrainingId);

    @Insert
    long insertProgramExercise(ProgramExercise programExercise);

    @Delete
    int deleteProgramExercise(ProgramExercise programExercise);

    @Delete
    int deleteProgramExercises(Collection<ProgramExercise> programExercises);

    @Update
    int updateProgramExercise(ProgramExercise programExercise);

    @Update
    int updateProgramExercises(Collection<ProgramExercise> programExercises);

    @Query("select * " +
            "from ProgramSet " +
            "where programExerciseId = :programExerciseId " +
            "order by sortOrder")
    LiveData<List<ProgramSet>> getProgramSetsForExercise(long programExerciseId);

    @Query("select * from ProgramSet where id = :id")
    LiveData<ProgramSet> getProgramSetById(long id);

    @Insert
    long insertProgramSet(ProgramSet programSet);

    @Delete
    int deleteProgramSet(ProgramSet programSet);

    @Update
    int updateProgramSet(ProgramSet programSet);
}