package ru.codingworkshop.gymm.data.wrapper;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Радик on 20.06.2017.
 */

public class ProgramTrainingWrapper extends BaseWrapper<ProgramTraining, ProgramExercise> {
    private ProgramTrainingRepository repository;

    public ProgramTrainingWrapper(@NonNull ProgramTrainingRepository repository) {
        Preconditions.checkNotNull(repository);

        this.repository = repository;
    }

    ProgramTrainingWrapper(@NonNull ProgramTraining programTraining, @NonNull ProgramTrainingRepository repository) {
        this(repository);
        setRoot(programTraining);
    }

    @Override
    protected void saveRoot() {
        repository.updateProgramTraining(getRoot());
    }

    @Override
    protected void saveChildren() {
        ChildrenSaver<ProgramExercise> saver = new ChildrenSaver<ProgramExercise>(repository.getProgramExercisesForTraining(getRoot()), getChildren()) {
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

    public LiveData<ProgramTrainingWrapper> createTraining() {
        BaseLoader<ProgramTrainingWrapper, ProgramTraining, ProgramExercise> loader = new BaseLoader<ProgramTrainingWrapper, ProgramTraining, ProgramExercise>(this) {
            @Override
            protected LiveData<ProgramTraining> getLiveRoot() {
                return repository.getDraftingProgramTraining();
            }

            @Override
            protected LiveData<List<ProgramExercise>> getLiveChildren(ProgramTraining root) { // FIXME this may not work because of insertion above
                return repository.getProgramExercisesForTraining(root);
            }

            @Override
            protected void runIfRootAbsent() {
                repository.insertProgramTraining(programTrainingInstance());
            }
        };

        return loader.load();
    }

    public LiveData<ProgramTrainingWrapper> load(long id) {
        BaseLoader<ProgramTrainingWrapper, ProgramTraining, ProgramExercise> loader = new BaseLoader<ProgramTrainingWrapper, ProgramTraining, ProgramExercise>(this) {
            @Override
            protected LiveData<ProgramTraining> getLiveRoot() {
                return repository.getProgramTrainingById(id);
            }

            @Override
            protected LiveData<List<ProgramExercise>> getLiveChildren() {
                return repository.getProgramExercisesForTraining(id);
            }
        };

        return loader.load();
    }

    public static ProgramTraining programTrainingInstance() {
        ProgramTraining programTraining = new ProgramTraining();
        programTraining.setDrafting(true);
        return programTraining;
    }
}
