package ru.codingworkshop.gymm.data.wrapper;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Preconditions;

import java.util.Collection;
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
    private SortableChildrenDelegate<ProgramExercise> childrenDelegate = new SortableChildrenDelegate<>();

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
        return childrenDelegate.getChildren();
    }

    public void setProgramExercises(Collection<? extends ProgramExercise> programExercises) {
        childrenDelegate.setChildren(programExercises);
    }

    public boolean hasProgramExercises() {
        return childrenDelegate.hasChildren();
    }

    @Deprecated
    public void addProgramExercise(@NonNull ProgramExercise programExercise) {
        childrenDelegate.add(programExercise);
    }

    public void restoreLastRemoved() {
        childrenDelegate.restoreLastRemoved();
    }

    public void removeProgramExercise(@NonNull ProgramExercise programExercise) {
        childrenDelegate.remove(programExercise);
    }

    public void moveProgramExercise(int from, int to) {
        childrenDelegate.move(from, to);
    }

    public void save(ProgramTrainingRepository repository) {
        Preconditions.checkState(hasProgramExercises(), "Must have children");

        repository.updateProgramTraining((ProgramTrainingEntity) programTraining);

        LiveData<List<ProgramExerciseEntity>> oldExercisesLive = repository.getProgramExercisesForTraining(programTraining);

        ExercisesDiff diff = new ExercisesDiff((List<ProgramExerciseEntity>)(List<?>) childrenDelegate.getChildren()) {
            @Override
            public void difference(List<ProgramExerciseEntity> toDelete, List<ProgramExerciseEntity> toUpdate, List<ProgramExerciseEntity> toInsert) {
                repository.updateProgramExercises(toUpdate);
                repository.deleteProgramExercises(toDelete);

                oldExercisesLive.removeObserver(this);
            }
        };

        oldExercisesLive.observeForever(diff);
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
        loader.addDependentSource( // FIXME this may not work because of insertion above
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

    private static abstract class ExercisesDiff extends ChildrenDiff<ProgramExerciseEntity> implements Observer<List<ProgramExerciseEntity>> {

        public ExercisesDiff(List<ProgramExerciseEntity> newChildren)
        {
            super(
                    null,
                    newChildren,
                    (oldOne, newOne) -> oldOne.getId() == newOne.getId() ? 0 : 1,
                    (oldOne, newOne) -> oldOne.getSortOrder() - newOne.getSortOrder()
            );
        }

        @Override
        public void onChanged(@Nullable List<ProgramExerciseEntity> programExerciseEntities) {
            oldChildren = programExerciseEntities;
            calculate();
        }
    }
}
