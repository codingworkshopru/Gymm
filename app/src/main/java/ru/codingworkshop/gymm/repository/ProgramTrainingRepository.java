package ru.codingworkshop.gymm.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.entity.common.Named;
import ru.codingworkshop.gymm.db.dao.ProgramTrainingDao;

import static ru.codingworkshop.gymm.db.GymmDatabase.INVALID_ID;

/**
 * Created by Радик on 21.06.2017.
 */

@Singleton
@SuppressWarnings("Guava")
public class ProgramTrainingRepository extends BaseRepository {
    private final ProgramTrainingDao dao;

    @Inject
    public ProgramTrainingRepository(ProgramTrainingDao dao, Executor executor) {
        super(executor);
        this.dao = dao;
    }

    public LiveData<List<ProgramTraining>> getProgramTrainings() {
        return dao.getProgramTrainings();
    }

    public LiveData<ProgramTraining> getProgramTrainingById(long id) {
        return dao.getProgramTrainingById(id);
    }

    public LiveData<ProgramTraining> getDraftingProgramTraining() {
        return dao.getDraftingProgramTraining();
    }

    public void insertProgramTraining(@NonNull ProgramTraining programTraining) {
        insert(programTraining, dao::insertProgramTraining, ProgramTrainingRepository::checkName);
    }

    public void deleteProgramTraining(@NonNull ProgramTraining programTraining) {
        delete(programTraining, dao::deleteProgramTraining);
    }

    public void updateProgramTraining(@NonNull ProgramTraining programTraining) {
        update(programTraining, dao::updateProgramTraining, ProgramTrainingRepository::checkName);
    }

    public LiveData<List<ProgramExercise>> getProgramExercisesForTraining(@NonNull ProgramTraining programTraining) {
        return getProgramExercisesForTraining(programTraining.getId());
    }

    public LiveData<List<ProgramExercise>> getProgramExercisesForTraining(long trainingId) {
        return dao.getProgramExercisesForTraining(trainingId);
    }

    public LiveData<ProgramExercise> getProgramExerciseById(long id) {
        return dao.getProgramExerciseById(id);
    }

    public LiveData<ProgramExercise> getDraftingProgramExercise(@NonNull ProgramTraining training) {
        return getDraftingProgramExercise(training.getId());
    }

    public LiveData<ProgramExercise> getDraftingProgramExercise(long programTrainingId) {
        Preconditions.checkArgument(isValidId(programTrainingId));
        return dao.getDraftingProgramExercise(programTrainingId);
    }

    public void insertProgramExercise(ProgramExercise programExercise) {
        insert(programExercise, dao::insertProgramExercise, ProgramTrainingRepository::checkProgramExercise);
    }

    public void updateProgramExercise(ProgramExercise programExercise) {
        update(programExercise, dao::updateProgramExercise, ProgramTrainingRepository::checkProgramExercise);
    }

    public void updateProgramExercises(Collection<ProgramExercise> exerciseEntities) {
        update(exerciseEntities, dao::updateProgramExercises, ProgramTrainingRepository::checkProgramExercise);
    }

    private static void checkProgramExercise(ProgramExercise programExercise) {
        Preconditions.checkArgument(isValidId(programExercise.getProgramTrainingId()));
        Preconditions.checkArgument(isValidId(programExercise.getExerciseId()));
    }

    public void deleteProgramExercise(ProgramExercise programExercise) {
        delete(programExercise, dao::deleteProgramExercise);
    }

    public void deleteProgramExercises(Collection<ProgramExercise> exerciseEntities) {
        delete(exerciseEntities, dao::deleteProgramExercises);
    }

    public LiveData<List<ProgramSet>> getProgramSetsForExercise(@NonNull ProgramExercise exercise) {
        return getProgramSetsForExercise(exercise.getId());
    }

    public LiveData<List<ProgramSet>> getProgramSetsForExercise(long id) {
        return dao.getProgramSetsForExercise(id);
    }

    public LiveData<List<ProgramSet>> getProgramSetsForTraining(long trainingId) {
        return dao.getProgramSetsForTraining(trainingId);
    }

    public LiveData<ProgramSet> getProgramSetById(long id) {
        return dao.getProgramSetById(id);
    }

    public void insertProgramSet(@NonNull ProgramSet set) {
        insert(set, dao::insertProgramSet, ProgramTrainingRepository::checkProgramSet);
    }

    public void insertProgramSets(@NonNull Collection<ProgramSet> sets) {
        insert(sets, dao::insertProgramSets, ProgramTrainingRepository::checkProgramSet);
    }

    public void updateProgramSet(@NonNull ProgramSet set) {
        update(set, dao::updateProgramSet, ProgramTrainingRepository::checkProgramSet);
    }

    public void updateProgramSets(@NonNull Collection<ProgramSet> sets) {
        update(sets, dao::updateProgramSets, ProgramTrainingRepository::checkProgramSet);
    }

    private static void checkProgramSet(@NonNull ProgramSet set) {
        Preconditions.checkArgument(set.getReps() != 0);
        Preconditions.checkArgument(isValidId(set.getProgramExerciseId()));
    }

    public void deleteProgramSet(@NonNull ProgramSet set) {
        delete(set, dao::deleteProgramSet);
    }

    public void deleteProgramSets(@NonNull Collection<ProgramSet> sets) {
        delete(sets, dao::deleteProgramSets);
    }

    private static void checkName(@NonNull Named named) {
        Preconditions.checkArgument(named.getName() != null && !named.getName().isEmpty());
    }

    private static boolean isValidId(long id) {
        return id != INVALID_ID;
    }
}
