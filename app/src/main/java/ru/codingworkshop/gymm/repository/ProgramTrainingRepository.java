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
import ru.codingworkshop.gymm.data.entity.common.Draftable;
import ru.codingworkshop.gymm.data.entity.common.Named;
import ru.codingworkshop.gymm.db.dao.ProgramTrainingDao;

import static ru.codingworkshop.gymm.db.GymmDatabase.isValidId;

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

    public LiveData<ProgramTraining> getProgramTrainingByName(String name) {
        return dao.getProgramTrainingByName(name);
    }

    public LiveData<ProgramTraining> getDraftingProgramTraining() {
        return dao.getDraftingProgramTraining();
    }

    // TODO training name must be unique; write feature to throw exception up from dao (insertChildren, updateChildren)
    public void insertProgramTraining(@NonNull ProgramTraining programTraining) {
        checkName(programTraining);
        checkIsDrafting(programTraining);
        insert(programTraining, dao::insertProgramTraining);
    }

    public void deleteProgramTraining(@NonNull ProgramTraining programTraining) {
        delete(programTraining, dao::deleteProgramTraining);
    }

    public void updateProgramTraining(@NonNull ProgramTraining programTraining) {
        checkName(programTraining);
        update(programTraining, dao::updateProgramTraining);
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
        checkProgramExercise(programExercise);
        checkIsDrafting(programExercise);
        insert(programExercise, dao::insertProgramExercise);
    }

    private void checkIsDrafting(Draftable draftable) {
        Preconditions.checkArgument(draftable.isDrafting());
    }

    public void updateProgramExercise(ProgramExercise programExercise) {
        checkProgramExercise(programExercise);
        update(programExercise, dao::updateProgramExercise);
    }

    public void updateProgramExercises(Collection<ProgramExercise> exerciseEntities) {
        applyToEach(exerciseEntities, ProgramTrainingRepository::checkProgramExercise);
        update(exerciseEntities, dao::updateProgramExercises);
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
        checkProgramSet(set);
        insert(set, dao::insertProgramSet);
    }

    public void insertProgramSets(@NonNull Collection<ProgramSet> sets) {
        applyToEach(sets, ProgramTrainingRepository::checkProgramSet);
        insert(sets, dao::insertProgramSets);
    }

    public void updateProgramSet(@NonNull ProgramSet set) {
        checkProgramSet(set);
        update(set, dao::updateProgramSet);
    }

    public void updateProgramSets(@NonNull Collection<ProgramSet> sets) {
        applyToEach(sets, ProgramTrainingRepository::checkProgramSet);
        update(sets, dao::updateProgramSets);
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
}
