package ru.codingworkshop.gymm.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Collection;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramExerciseEntity;
import ru.codingworkshop.gymm.data.entity.ProgramSetEntity;
import ru.codingworkshop.gymm.data.entity.ProgramTrainingEntity;

import static android.arch.persistence.room.OnConflictStrategy.FAIL;

/**
 * Created by Радик on 04.06.2017.
 */

@Dao
public interface ProgramTrainingDao {
    @Query("select * from ProgramTraining")
    LiveData<List<ProgramTrainingEntity>> getProgramTrainings();

    @Query("select * from ProgramTraining where id = :id")
    LiveData<ProgramTrainingEntity> getProgramTrainingById(long id);

    @Query("select * from ProgramTraining where drafting = 1 limit 1")
    LiveData<ProgramTrainingEntity> getDraftingProgramTraining();

    @Insert(onConflict = FAIL)
    long insertProgramTraining(ProgramTrainingEntity programTraining);

    @Delete
    int deleteProgramTraining(ProgramTrainingEntity programTraining);

    @Update(onConflict = FAIL)
    int updateProgramTraining(ProgramTrainingEntity programTraining);

    @Query("select * " +
            "from ProgramExercise " +
            "where programTrainingId = :programTrainingId " +
            "order by sortOrder")
    LiveData<List<ProgramExerciseEntity>> getProgramExercisesForTraining(long programTrainingId);

    @Query("select * from ProgramExercise where id = :id")
    LiveData<ProgramExerciseEntity> getProgramExerciseById(long id);

    @Query("select * from ProgramExercise where drafting = 1 and programTrainingId = :programTrainingId limit 1")
    LiveData<ProgramExerciseEntity> getDraftingProgramExercise(long programTrainingId);

    @Insert
    long insertProgramExercise(ProgramExerciseEntity programExercise);

    @Delete
    int deleteProgramExercise(ProgramExerciseEntity programExercise);

    @Delete
    int deleteProgramExercises(Collection<ProgramExerciseEntity> programExercises);

    @Update
    int updateProgramExercise(ProgramExerciseEntity programExercise);

    @Update
    int updateProgramExercises(Collection<ProgramExerciseEntity> programExercises);

    @Query("select * " +
            "from ProgramSet " +
            "where programExerciseId = :programExerciseId " +
            "order by sortOrder")
    LiveData<List<ProgramSetEntity>> getProgramSetsForExercise(long programExerciseId);

    @Query("select * from ProgramSet where id = :id")
    LiveData<ProgramSetEntity> getProgramSetById(long id);

    @Insert
    long insertProgramSet(ProgramSetEntity programSet);

    @Delete
    int deleteProgramSet(ProgramSetEntity programSet);

    @Update
    int updateProgramSet(ProgramSetEntity programSet);
}
