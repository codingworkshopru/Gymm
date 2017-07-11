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

import ru.codingworkshop.gymm.data.entity.ProgramExerciseEntity;
import ru.codingworkshop.gymm.data.entity.ProgramSetEntity;
import ru.codingworkshop.gymm.data.entity.ProgramTrainingEntity;
import ru.codingworkshop.gymm.data.model.ProgramExercise;
import ru.codingworkshop.gymm.data.model.ProgramSet;
import ru.codingworkshop.gymm.data.model.ProgramTraining;
import ru.codingworkshop.gymm.data.model.common.Model;
import ru.codingworkshop.gymm.data.model.common.Named;
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

    public LiveData<List<ProgramTrainingEntity>> getProgramTrainings() {
        return dao.getProgramTrainings();
    }

    public LiveData<ProgramTrainingEntity> getProgramTrainingById(long id) {
        return dao.getProgramTrainingById(id);
    }

    public LiveData<ProgramTrainingEntity> getDraftingProgramTraining() {
        return dao.getDraftingProgramTraining();
    }

    public void insertProgramTraining(@NonNull ProgramTrainingEntity programTrainingEntity) {
        checkName(programTrainingEntity);
        performInsert(dao::insertProgramTraining, programTrainingEntity);
    }

    public void deleteProgramTraining(@NonNull ProgramTrainingEntity programTrainingEntity) {
        performDelete(dao::deleteProgramTraining, programTrainingEntity);
    }

    public void updateProgramTraining(@NonNull ProgramTrainingEntity programTrainingEntity) {
        checkName(programTrainingEntity);
        performUpdate(dao::updateProgramTraining, programTrainingEntity);
    }

    public LiveData<List<ProgramExerciseEntity>> getProgramExercisesForTraining(@NonNull ProgramTraining programTraining) {
        return dao.getProgramExercisesForTraining(programTraining.getId());
    }

    public LiveData<List<ProgramExerciseEntity>> getProgramExercisesForTraining(long trainingId) {
        return dao.getProgramExercisesForTraining(trainingId);
    }

    public LiveData<ProgramExerciseEntity> getProgramExerciseById(long id) {
        return dao.getProgramExerciseById(id);
    }

    public LiveData<ProgramExerciseEntity> getDraftingProgramExercise(@NonNull ProgramTraining training) {
        checkModelId(training);
        return dao.getDraftingProgramExercise(training.getId());
    }

    public void insertProgramExercise(ProgramExerciseEntity programExerciseEntity) {
        checkProgramExercise(programExerciseEntity);
        performInsert(dao::insertProgramExercise, programExerciseEntity);
    }

    public void updateProgramExercise(ProgramExerciseEntity programExerciseEntity) {
        checkProgramExercise(programExerciseEntity);
        performUpdate(dao::updateProgramExercise, programExerciseEntity);
    }

    public void updateProgramExercises(Collection<ProgramExerciseEntity> exerciseEntities) {
        for (ProgramExercise exercise : exerciseEntities) {
            checkProgramExercise(exercise);
        }
        performUpdate(dao::updateProgramExercises, exerciseEntities);
    }

    private void checkProgramExercise(ProgramExercise programExercise) {
        Preconditions.checkArgument(isValidId(programExercise.getProgramTrainingId()));
        Preconditions.checkArgument(isValidId(programExercise.getExerciseId()));
    }

    public void deleteProgramExercise(ProgramExerciseEntity programExerciseEntity) {
        performDelete(dao::deleteProgramExercise, programExerciseEntity);
    }

    public void deleteProgramExercises(Collection<ProgramExerciseEntity> exerciseEntities) {
        performDelete(dao::deleteProgramExercises, exerciseEntities);
    }

    public LiveData<List<ProgramSetEntity>> getProgramSetsForExercise(@NonNull ProgramExercise exercise) {
        return dao.getProgramSetsForExercise(exercise.getId());
    }

    public LiveData<ProgramSetEntity> getProgramSetById(long id) {
        return dao.getProgramSetById(id);
    }

    public void insertProgramSet(@NonNull ProgramSetEntity set) {
        checkProgramSet(set);
        performInsert(dao::insertProgramSet, set);
    }

    public void updateProgramSet(@NonNull ProgramSetEntity set) {
        checkProgramSet(set);
        performUpdate(dao::updateProgramSet, set);
    }

    private void checkProgramSet(@NonNull ProgramSet set) {
        Preconditions.checkArgument(set.getReps() != 0);
        Preconditions.checkArgument(isValidId(set.getProgramExerciseId()));
    }

    public void deleteProgramSet(@NonNull ProgramSetEntity set) {
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
