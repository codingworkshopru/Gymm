package ru.codingworkshop.gymm.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.entity.common.Named;
import ru.codingworkshop.gymm.db.dao.ProgramTrainingDao;
import timber.log.Timber;

import static ru.codingworkshop.gymm.db.GymmDatabase.INVALID_ID;

/**
 * Created by Радик on 21.06.2017.
 */

@Singleton
public class ProgramTrainingRepository {
    private final ProgramTrainingDao dao;
    private final Executor executor;

    @Inject
    public ProgramTrainingRepository(ProgramTrainingDao dao, Executor executor) {
        this.dao = dao;
        this.executor = executor;
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
        checkName(programTraining);
        performInsert(dao::insertProgramTraining, programTraining);
    }

    public void deleteProgramTraining(@NonNull ProgramTraining programTraining) {
        performDelete(dao::deleteProgramTraining, programTraining);
    }

    public void updateProgramTraining(@NonNull ProgramTraining programTraining) {
        checkName(programTraining);
        performUpdate(dao::updateProgramTraining, programTraining);
    }

    public LiveData<List<ProgramExercise>> getProgramExercisesForTraining(@NonNull ProgramTraining programTraining) {
        return dao.getProgramExercisesForTraining(programTraining.getId());
    }

    public LiveData<List<ProgramExercise>> getProgramExercisesForTraining(long trainingId) {
        return dao.getProgramExercisesForTraining(trainingId);
    }

    public LiveData<ProgramExercise> getProgramExerciseById(long id) {
        return dao.getProgramExerciseById(id);
    }

    public LiveData<ProgramExercise> getDraftingProgramExercise(@NonNull ProgramTraining training) {
        checkModelId(training);
        return dao.getDraftingProgramExercise(training.getId());
    }

    public void insertProgramExercise(ProgramExercise programExercise) {
        checkProgramExercise(programExercise);
        performInsert(dao::insertProgramExercise, programExercise);
    }

    public void updateProgramExercise(ProgramExercise programExercise) {
        checkProgramExercise(programExercise);
        performUpdate(dao::updateProgramExercise, programExercise);
    }

    public void updateProgramExercises(Collection<ProgramExercise> exerciseEntities) {
        for (ProgramExercise exercise : exerciseEntities) {
            checkProgramExercise(exercise);
        }
        performUpdate(dao::updateProgramExercises, exerciseEntities);
    }

    private void checkProgramExercise(ProgramExercise programExercise) {
        Preconditions.checkArgument(isValidId(programExercise.getProgramTrainingId()));
        Preconditions.checkArgument(isValidId(programExercise.getExerciseId()));
    }

    public void deleteProgramExercise(ProgramExercise programExercise) {
        performDelete(dao::deleteProgramExercise, programExercise);
    }

    public void deleteProgramExercises(Collection<ProgramExercise> exerciseEntities) {
        performDelete(dao::deleteProgramExercises, exerciseEntities);
    }

    public LiveData<List<ProgramSet>> getProgramSetsForExercise(@NonNull ProgramExercise exercise) {
        return dao.getProgramSetsForExercise(exercise.getId());
    }

    public LiveData<ProgramSet> getProgramSetById(long id) {
        return dao.getProgramSetById(id);
    }

    public void insertProgramSet(@NonNull ProgramSet set) {
        checkProgramSet(set);
        performInsert(dao::insertProgramSet, set);
    }

    public void updateProgramSet(@NonNull ProgramSet set) {
        checkProgramSet(set);
        performUpdate(dao::updateProgramSet, set);
    }

    private void checkProgramSet(@NonNull ProgramSet set) {
        Preconditions.checkArgument(set.getReps() != 0);
        Preconditions.checkArgument(isValidId(set.getProgramExerciseId()));
    }

    public void deleteProgramSet(@NonNull ProgramSet set) {
        performDelete(dao::deleteProgramSet, set);
    }

    private <F> void performInsert(Function<F, Long> insert, F entity) {
        executor.execute(() -> checkInsertion(insert.apply(entity)));
    }

    private <F> void performDelete(Function<F, Integer> delete, F entity) {
        perform(delete, entity, entity.getClass(), "delete");
    }

    private <F> void performDelete(Function<Collection<F>, Integer> delete, Collection<F> entities) {
        if (entities.isEmpty()) return;
        perform(delete, entities, Iterables.getFirst(entities, null).getClass(), "delete");
    }

    private <F> void performUpdate(Function<F, Integer> update, F entity) {
        perform(update, entity, entity.getClass(), "update");
    }

    private <F> void performUpdate(Function<Collection<F>, Integer> update, Collection<F> entities) {
        if (entities.isEmpty()) return;
        perform(update, entities, Iterables.getFirst(entities, null).getClass(), "update");
    }

    private <F> void perform(Function<F, Integer> operation, F operand, Class<?> cl, String operationName) {
        executor.execute(() -> {
            int rowsAffected = operation.apply(operand);
            Timber.i("%d rows %sd in %s", rowsAffected, operationName, cl.getSimpleName());
        });
    }

    private long checkInsertion(long rowId) {
        Preconditions.checkState(rowId != -1);
        return rowId;
    }

    private void checkName(@NonNull Named named) {
        Preconditions.checkArgument(named.getName() != null && !named.getName().isEmpty());
    }

    private void checkModelId(Model model) {
        Preconditions.checkArgument(isValidId(model.getId()));
    }

    private boolean isValidId(long id) {
        return id != INVALID_ID;
    }
}