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
    public ProgramTrainingRepository(@NonNull ProgramTrainingDao dao, @NonNull InsertDelegate insertDelegate) {
        this.dao = dao;
        this.insertDelegate = insertDelegate;
    }

    public LiveData<List<ProgramTraining>> getProgramTrainings() {
        return dao.getProgramTrainings();
    }

    public ProgramTraining getProgramTrainingById(long trainingId) {
        return dao.getProgramTrainingById(trainingId);
    }

    public LiveData<ProgramTraining> getProgramTrainingByName(String name) {
        return dao.getProgramTrainingByName(name);
    }

    public long insertProgramTraining(@NonNull ProgramTraining programTraining) {
        checkName(programTraining);
        return insertDelegate.insert(programTraining, dao::insertProgramTraining);
    }

    public int deleteProgramTraining(@NonNull ProgramTraining programTraining) {
        return dao.deleteProgramTraining(programTraining);
    }

    public int updateProgramTraining(@NonNull ProgramTraining programTraining) {
        checkName(programTraining);
        return dao.updateProgramTraining(programTraining);
    }

    public List<ProgramExercise> getProgramExercisesForTraining(long trainingId) {
        return dao.getProgramExercisesForTraining(trainingId);
    }

    public List<Long> insertProgramExercises(@NonNull Collection<ProgramExercise> programExercises) {
        for (ProgramExercise ex : programExercises) {
            checkProgramExercise(ex);
        }
        return insertDelegate.insert(programExercises, dao::insertProgramExercises);
    }

    public int updateProgramExercises(Collection<ProgramExercise> exerciseEntities) {
        for (ProgramExercise ex : exerciseEntities) {
            checkProgramExercise(ex);
        }
        return dao.updateProgramExercises(exerciseEntities);
    }

    private static void checkProgramExercise(ProgramExercise programExercise) {
        Preconditions.checkArgument(isValidId(programExercise.getProgramTrainingId()));
        Long exerciseId = programExercise.getExerciseId();
        Preconditions.checkArgument(isValidId(exerciseId));
    }

    public int deleteProgramExercises(Collection<ProgramExercise> exerciseEntities) {
        return dao.deleteProgramExercises(exerciseEntities);
    }

    public List<ProgramSet> getProgramSetsForTraining(long trainingId) {
        return dao.getProgramSetsForTraining(trainingId);
    }

    public List<Long> insertProgramSets(@NonNull Collection<ProgramSet> sets) {
        for (ProgramSet set : sets) {
            checkProgramSet(set);
        }
        return insertDelegate.insert(sets, dao::insertProgramSets);
    }

    public int updateProgramSets(@NonNull Collection<ProgramSet> sets) {
        for (ProgramSet set : sets) {
            checkProgramSet(set);
        }
        return dao.updateProgramSets(sets);
    }

    private static void checkProgramSet(@NonNull ProgramSet set) {
        Preconditions.checkArgument(set.getReps() != 0);
        Preconditions.checkArgument(isValidId(set.getProgramExerciseId()));
    }

    public int deleteProgramSets(@NonNull Collection<ProgramSet> sets) {
        return dao.deleteProgramSets(sets);
    }

    private static void checkName(@NonNull Named named) {
        Preconditions.checkArgument(named.getName() != null && !named.getName().isEmpty());
    }
}
