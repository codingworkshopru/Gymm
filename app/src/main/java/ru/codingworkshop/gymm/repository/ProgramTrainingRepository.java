package ru.codingworkshop.gymm.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.entity.common.Named;
import ru.codingworkshop.gymm.db.dao.ProgramTrainingDao;

import static ru.codingworkshop.gymm.db.GymmDatabase.isValidId;

/**
 * Created by Радик on 21.06.2017.
 */

@Singleton
@SuppressWarnings("Guava")
public class ProgramTrainingRepository {
    private final ProgramTrainingDao dao;
    private final InsertDelegate insertDelegate;

    @Inject
    public ProgramTrainingRepository(ProgramTrainingDao dao, InsertDelegate insertDelegate) {
        this.dao = dao;
        this.insertDelegate = insertDelegate;
    }

    public LiveData<List<ProgramTraining>> getProgramTrainings() {
        return dao.getProgramTrainings();
    }

    public Flowable<ProgramTraining> getProgramTrainingById(long id) {
        return dao.getProgramTrainingById(id);
    }

    public LiveData<ProgramTraining> getProgramTrainingByName(String name) {
        return dao.getProgramTrainingByName(name);
    }

    public long insertProgramTraining(@NonNull ProgramTraining programTraining) {
        checkName(programTraining);
        return dao.insertProgramTraining(programTraining);
    }

    public void deleteProgramTraining(@NonNull ProgramTraining programTraining) {
        dao.deleteProgramTraining(programTraining);
    }

    public void updateProgramTraining(@NonNull ProgramTraining programTraining) {
        checkName(programTraining);
        dao.updateProgramTraining(programTraining);
    }

    public Flowable<List<ProgramExercise>> getProgramExercisesForTraining(@NonNull ProgramTraining programTraining) {
        return getProgramExercisesForTraining(programTraining.getId());
    }

    public Flowable<List<ProgramExercise>> getProgramExercisesForTraining(long trainingId) {
        return dao.getProgramExercisesForTraining(trainingId);
    }

    public List<Long> insertProgramExercises(@NonNull Collection<ProgramExercise> programExercises) {
        for (ProgramExercise ex : programExercises) {
            checkProgramExercise(ex);
        }
        return insertDelegate.insert(programExercises, dao::insertProgramExercises);
    }

    public void updateProgramExercises(Collection<ProgramExercise> exerciseEntities) {
        for (ProgramExercise ex : exerciseEntities) {
            checkProgramExercise(ex);
        }
        dao.updateProgramExercises(exerciseEntities);
    }

    private static void checkProgramExercise(ProgramExercise programExercise) {
        Preconditions.checkArgument(isValidId(programExercise.getProgramTrainingId()));
        Long exerciseId = programExercise.getExerciseId();
        Preconditions.checkArgument(isValidId(exerciseId));
    }

    public void deleteProgramExercises(Collection<ProgramExercise> exerciseEntities) {
        dao.deleteProgramExercises(exerciseEntities);
    }

    public Flowable<List<ProgramSet>> getProgramSetsForTraining(long trainingId) {
        return dao.getProgramSetsForTraining(trainingId);
    }

    public List<Long> insertProgramSets(@NonNull Collection<ProgramSet> sets) {
        for (ProgramSet set : sets) {
            checkProgramSet(set);
        }
        return dao.insertProgramSets(sets);
    }

    public void updateProgramSets(@NonNull Collection<ProgramSet> sets) {
        for (ProgramSet set : sets) {
            checkProgramSet(set);
        }
        dao.updateProgramSets(sets);
    }

    private static void checkProgramSet(@NonNull ProgramSet set) {
        Preconditions.checkArgument(set.getReps() != 0);
        Preconditions.checkArgument(isValidId(set.getProgramExerciseId()));
    }

    public void deleteProgramSets(@NonNull Collection<ProgramSet> sets) {
        dao.deleteProgramSets(sets);
    }

    private static void checkName(@NonNull Named named) {
        Preconditions.checkArgument(named.getName() != null && !named.getName().isEmpty());
    }
}
