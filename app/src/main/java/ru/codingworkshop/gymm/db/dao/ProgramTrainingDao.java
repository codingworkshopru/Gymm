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
    @Query("select * from ProgramTraining order by name")
    LiveData<List<ProgramTraining>> getProgramTrainings();

    @Query("select * from ProgramTraining where id = :id")
    ProgramTraining getProgramTrainingById(long id);

    @Query("select * from ProgramTraining where name = :name")
    LiveData<ProgramTraining> getProgramTrainingByName(String name);

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
    List<ProgramExercise> getProgramExercisesForTraining(long programTrainingId);

    @Insert
    List<Long> insertProgramExercises(Collection<ProgramExercise> programExercises);

    @Delete
    int deleteProgramExercises(Collection<ProgramExercise> programExercises);

    @Update
    int updateProgramExercises(Collection<ProgramExercise> programExercises);

    @Query("select ps.* " +
            "from ProgramSet ps " +
            "join ProgramExercise pe on pe.id = ps.programExerciseId " +
            "where pe.programTrainingId = :programTrainingId " +
            "order by pe.sortOrder, ps.sortOrder")
    List<ProgramSet> getProgramSetsForTraining(long programTrainingId);

    @Insert
    List<Long> insertProgramSets(Collection<ProgramSet> programSets);

    @Delete
    int deleteProgramSets(Collection<ProgramSet> programSets);

    @Update
    int updateProgramSets(Collection<ProgramSet> programSets);
}
