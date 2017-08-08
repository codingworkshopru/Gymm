package ru.codingworkshop.gymm.data.wrapper;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Радик on 13.07.2017.
 */

public class ProgramExerciseWrapper extends BaseWrapper<ProgramExercise, ProgramSet> {
    private Exercise exercise;
    private ProgramTrainingRepository programTrainingRepository;
    private ExercisesRepository exercisesRepository;

    public ProgramExerciseWrapper(@NonNull ProgramTrainingRepository programTrainingRepository, @NonNull ExercisesRepository exercisesRepository) {
        Preconditions.checkNotNull(programTrainingRepository);
        Preconditions.checkNotNull(exercisesRepository);
        this.programTrainingRepository = programTrainingRepository;
        this.exercisesRepository = exercisesRepository;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(@NonNull Exercise exercise) {
        this.exercise = exercise;
    }

    @Override
    protected void saveRoot() {
        programTrainingRepository.updateProgramExercise(getRoot());
    }

    @Override
    protected void saveChildren() {
        ChildrenSaver<ProgramSet> saver = new ChildrenSaver<ProgramSet>(programTrainingRepository.getProgramSetsForExercise(getRoot()), getChildren()) {
            @Override
            public void update(List<ProgramSet> toUpdate) {
                programTrainingRepository.updateProgramSets(toUpdate);
            }

            @Override
            public void delete(List<ProgramSet> toDelete) {
                programTrainingRepository.deleteProgramSets(toDelete);
            }

            @Override
            public void insert(List<ProgramSet> toInsert) {
                programTrainingRepository.insertProgramSets(toInsert);
            }
        };

        saver.save();
    }

    public static ProgramExercise programExerciseInstance(long programTrainingId) {
        ProgramExercise programExercise = new ProgramExercise();
        programExercise.setProgramTrainingId(programTrainingId);
        programExercise.setDrafting(true);
        return programExercise;
    }

    public LiveData<ProgramExerciseWrapper> createProgramExercise(long programTrainingId) {
        ProgramExerciseWrapperLoader loader = new ProgramExerciseWrapperLoader(this, exercisesRepository) {
            @Override
            protected LiveData<ProgramExercise> getLiveRoot() {
                return programTrainingRepository.getDraftingProgramExercise(programTrainingId);
            }

            @Override
            protected LiveData<List<ProgramSet>> getLiveChildren(ProgramExercise root) {
                return programTrainingRepository.getProgramSetsForExercise(root);
            }

            @Override
            protected void runIfRootAbsent() {
                programTrainingRepository.insertProgramExercise(programExerciseInstance(programTrainingId));
            }
        };

        return loader.load();
    }

    public LiveData<ProgramExerciseWrapper> load(long programExerciseId) {
        ProgramExerciseWrapperLoader loader = new ProgramExerciseWrapperLoader(this, exercisesRepository) {
            @Override
            protected LiveData<ProgramExercise> getLiveRoot() {
                return programTrainingRepository.getProgramExerciseById(programExerciseId);
            }

            @Override
            protected LiveData<List<ProgramSet>> getLiveChildren() {
                return programTrainingRepository.getProgramSetsForExercise(programExerciseId);
            }
        };

        return loader.load();
    }

    private static abstract class ProgramExerciseWrapperLoader extends BaseLoader<ProgramExerciseWrapper, ProgramExercise, ProgramSet> {
        private ExercisesRepository exercisesRepository;

        public ProgramExerciseWrapperLoader(ProgramExerciseWrapper wrapper, ExercisesRepository exercisesRepository) {
            super(wrapper);
            this.exercisesRepository = exercisesRepository;
        }

        @Override
        protected void addSources() {
            super.addSources();
            addExercise();
        }

        private void addExercise() {
            addDependentSource(this::getLiveExercise, ProgramExerciseWrapper::setExercise);
        }

        private LiveData<Exercise> getLiveExercise(ProgramExercise root) {
            return exercisesRepository.getExerciseById(root.getExerciseId());
        }
    }
}
