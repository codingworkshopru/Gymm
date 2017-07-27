package ru.codingworkshop.gymm.data.wrapper;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Радик on 20.06.2017.
 */

public class ProgramTrainingWrapper {
    private ProgramTraining programTraining;
    private SortableChildrenDelegate<ProgramExercise> childrenDelegate = new SortableChildrenDelegate<>();

    private ProgramTrainingWrapper() {}

    ProgramTrainingWrapper(@NonNull ProgramTraining programTraining) {
        this.programTraining = programTraining;
    }

    public ProgramTraining getProgramTraining() {
        return programTraining;
    }

    public void setProgramTraining(@NonNull ProgramTraining programTraining) {
        this.programTraining = programTraining;
    }

    public List<ProgramExercise> getProgramExercises() {
        return childrenDelegate.getChildren();
    }

    public void setProgramExercises(Collection<ProgramExercise> programExercises) {
        childrenDelegate.setChildren(programExercises);
    }

    public boolean hasProgramExercises() {
        return childrenDelegate.hasChildren();
    }

    @Deprecated
    public void addProgramExercise(@NonNull ProgramExercise programExercise) {
        childrenDelegate.add(programExercise);
    }

    public void removeProgramExercise(@NonNull ProgramExercise programExercise) {
        childrenDelegate.remove(programExercise);
    }

    public void moveProgramExercise(int from, int to) {
        childrenDelegate.move(from, to);
    }

    public void restoreLastRemoved() {
        childrenDelegate.restoreLastRemoved();
    }

    public void save(@NonNull ProgramTrainingRepository repository) {
        Preconditions.checkState(hasProgramExercises(), "Must have children");

        repository.updateProgramTraining(programTraining);
        ChildrenSaver<ProgramExercise> saver = new ChildrenSaver<ProgramExercise>(repository.getProgramExercisesForTraining(programTraining), getProgramExercises()) {
            @Override
            public void update(List<ProgramExercise> toUpdate) {
                repository.updateProgramExercises(toUpdate);
            }

            @Override
            public void delete(List<ProgramExercise> toDelete) {
                repository.deleteProgramExercises(toDelete);
            }

            @Override
            public void insert(List<ProgramExercise> toInsert) {
            }
        };

        saver.save();
    }

    public static ProgramTraining createTraining() {
        ProgramTraining programTraining = new ProgramTraining();
        programTraining.setDrafting(true);
        return programTraining;
    }

    public static LiveData<ProgramTrainingWrapper> createTraining(ProgramTrainingRepository repository) {
        Loader<ProgramTrainingWrapper> loader = new Loader<>(ProgramTrainingWrapper::new);

        LiveData<ProgramTraining> draftingProgramTraining = repository.getDraftingProgramTraining();
        loader.addSource(
                draftingProgramTraining,
                ProgramTrainingWrapper::setProgramTraining,
                () -> repository.insertProgramTraining(createTraining())
        );

        loader.addDependentSource( // FIXME this may not work because of insertion above
                draftingProgramTraining,
                repository::getProgramExercisesForTraining,
                ProgramTrainingWrapper::setProgramExercises
        );

        return loader.load();
    }

    public static LiveData<ProgramTrainingWrapper> load(long id, ProgramTrainingRepository repository) {
        Loader<ProgramTrainingWrapper> loader = new Loader<>(ProgramTrainingWrapper::new);

        loader.addSource(repository.getProgramTrainingById(id),  ProgramTrainingWrapper::setProgramTraining);
        loader.addSource(repository.getProgramExercisesForTraining(id), ProgramTrainingWrapper::setProgramExercises);

        return loader.load();
    }
}
