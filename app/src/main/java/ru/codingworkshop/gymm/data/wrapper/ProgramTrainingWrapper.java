package ru.codingworkshop.gymm.data.wrapper;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramExerciseEntity;
import ru.codingworkshop.gymm.data.entity.ProgramTrainingEntity;
import ru.codingworkshop.gymm.data.model.ProgramExercise;
import ru.codingworkshop.gymm.data.model.ProgramTraining;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Радик on 20.06.2017.
 */

public class ProgramTrainingWrapper {
    private ProgramTraining programTraining;
    private List<ProgramExercise> programExercises = Lists.newArrayList();
    private ProgramExercise lastRemoved;

    @SuppressWarnings("unused")
    private ProgramTrainingWrapper() {}

    ProgramTrainingWrapper(@NonNull ProgramTraining programTraining) {
        this.programTraining = programTraining;
    }

    public ProgramTraining getProgramTraining() {
        return programTraining;
    }

    public void setProgramTraining(ProgramTraining programTraining) {
        this.programTraining = programTraining;
    }

    public List<ProgramExercise> getProgramExercises() {
        return Collections.unmodifiableList(programExercises);
    }

    public void setProgramExercises(Collection<? extends ProgramExercise> programExercises) {
        this.programExercises = Lists.newArrayList(programExercises);
    }

    public boolean hasProgramExercises() {
        return !programExercises.isEmpty();
    }

    @Deprecated
    public void addProgramExercise(@NonNull ProgramExercise programExercise) {
        programExercise.setSortOrder(programExercises.size());
        programExercises.add(programExercise);
    }

    public void restoreLastRemoved() {
        Preconditions.checkNotNull(lastRemoved);
        insertProgramExercise(lastRemoved.getSortOrder(), lastRemoved);
    }

    public void removeProgramExercise(@NonNull ProgramExercise programExercise) {
        programExercises.remove(programExercise);
        lastRemoved = programExercise;
        updateSortOrders(programExercise.getSortOrder());
    }

    public void moveProgramExercise(int from, int to) {
        Preconditions.checkElementIndex(from, programExercises.size());
        Preconditions.checkElementIndex(to, programExercises.size());
        ProgramExercise exercise = programExercises.remove(from);
        programExercises.add(to, exercise);
        updateSortOrders(Math.min(from, to), Math.max(from, to));
    }

    private void insertProgramExercise(int position, ProgramExercise exercise) {
        programExercises.add(position, exercise);
        updateSortOrders(position);
    }

    private void updateSortOrders(int start) {
        updateSortOrders(start, programExercises.size() - 1);
    }

    private void updateSortOrders(int start, int end) {
        for (int i = start; i <= end; i++)
            programExercises.get(i).setSortOrder(i);
    }

    public void save(ProgramTrainingRepository repository) {
        Preconditions.checkState(hasProgramExercises(), "Must have children");

        repository.updateProgramTraining((ProgramTrainingEntity) programTraining);

        repository.getProgramExercisesForTraining(programTraining).observeForever(oldExercises -> {
            if (oldExercises != null && !oldExercises.isEmpty()) {
                DiffUtil.DiffResult difference = DiffUtil.calculateDiff(new DiffIdCallback(oldExercises, programExercises));
                List<ProgramExerciseEntity> toDelete = Lists.newLinkedList();
                difference.dispatchUpdatesTo(new ListUpdateCallback() {

                    public void onRemoved(int position, int count) {
                        toDelete.addAll(oldExercises.subList(position, position + count));
                    }

                    public void onMoved(int fromPosition, int toPosition) {}
                    public void onInserted(int position, int count) {}
                    public void onChanged(int position, int count, Object payload) {}
                });

                if (!toDelete.isEmpty())
                    repository.deleteProgramExercises(toDelete);

                List<ProgramExerciseEntity> toUpdate = Lists.newLinkedList();
                for (int i = 0; i < programExercises.size(); i++) {
                    if (oldExercises.get(i).getId() != programExercises.get(i).getId()) {
                        toUpdate.add((ProgramExerciseEntity) programExercises.get(i));
                    }
                }

                if (!toUpdate.isEmpty())
                    repository.updateProgramExercises(toUpdate);
            }
        });
    }

    public static ProgramTrainingEntity createTraining() {
        ProgramTrainingEntity programTraining = new ProgramTrainingEntity();
        programTraining.setDrafting(true);
        return programTraining;
    }

    public static LiveData<ProgramTrainingWrapper> createTraining(ProgramTrainingRepository repository) {
        Loader<ProgramTrainingWrapper> loader = new Loader<>(ProgramTrainingWrapper.class);

        LiveData<ProgramTrainingEntity> draftingProgramTraining = repository.getDraftingProgramTraining();
        loader.addSource(draftingProgramTraining, (wrapper, training) -> {
            if (training == null) {
                repository.insertProgramTraining(createTraining());
            } else {
                wrapper.setProgramTraining(training);
            }
        });
        loader.addDependentSource(
                draftingProgramTraining,
                repository::getProgramExercisesForTraining,
                ProgramTrainingWrapper::setProgramExercises
        );

        return loader.load();
    }

    public static LiveData<ProgramTrainingWrapper> load(long id, ProgramTrainingRepository repository) {
        Loader<ProgramTrainingWrapper> loader = new Loader<>(ProgramTrainingWrapper.class);

        loader.addSource(repository.getProgramTrainingById(id), ProgramTrainingWrapper::setProgramTraining);
        loader.addSource(repository.getProgramExercisesForTraining(id), ProgramTrainingWrapper::setProgramExercises);

        return loader.load();
    }
}
