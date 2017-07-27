package ru.codingworkshop.gymm.data.wrapper;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Радик on 13.07.2017.
 */

public class ProgramExerciseWrapper {
    private ProgramExercise programExercise;
    private Exercise exercise;
    private SortableChildrenDelegate<ProgramSet> childrenDelegate = new SortableChildrenDelegate<>();

    public ProgramExercise getProgramExercise() {
        return programExercise;
    }

    public void setProgramExercise(@NonNull ProgramExercise exercise) {
        this.programExercise = exercise;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(@NonNull Exercise exercise) {
        this.exercise = exercise;
    }

    public List<ProgramSet> getProgramSets() {
        return childrenDelegate.getChildren();
    }

    public void setProgramSets(@NonNull Collection<ProgramSet> programSets) {
        childrenDelegate.setChildren(programSets);
    }

    public boolean hasProgramSets() {
        return childrenDelegate.hasChildren();
    }

    public void addProgramSet(@NonNull ProgramSet programSet) {
        childrenDelegate.add(programSet);
    }

    public void removeProgramSet(@NonNull ProgramSet programSet) {
        childrenDelegate.remove(programSet);
    }

    public void removeProgramSet(int index) {
        childrenDelegate.remove(index);
    }

    public void move(int from, int to) {
        childrenDelegate.move(from, to);
    }

    public void restoreLastRemoved() {
        childrenDelegate.restoreLastRemoved();
    }

    public void save(@NonNull ProgramTrainingRepository repository) {
        Preconditions.checkState(hasProgramSets(), "Must have children");

        repository.updateProgramExercise(programExercise);
        ChildrenSaver<ProgramSet> saver = new ChildrenSaver<ProgramSet>(repository.getProgramSetsForExercise(programExercise), getProgramSets()) {
            @Override
            public void update(List<ProgramSet> toUpdate) {
                repository.updateProgramSets(toUpdate);
            }

            @Override
            public void delete(List<ProgramSet> toDelete) {
                repository.deleteProgramSets(toDelete);
            }

            @Override
            public void insert(List<ProgramSet> toInsert) {
                repository.insertProgramSets(toInsert);
            }
        };

        saver.save();
    }

    public static ProgramExercise createProgramExercise(long programTrainingId) {
        ProgramExercise programExercise = new ProgramExercise();
        programExercise.setProgramTrainingId(programTrainingId);
        programExercise.setDrafting(true);
        return programExercise;
    }

    public static LiveData<ProgramExerciseWrapper> createProgramExercise(ProgramTrainingRepository repository, ExercisesRepository exercisesRepository, long programTrainingId) {
        Loader<ProgramExerciseWrapper> loader = new Loader<>(ProgramExerciseWrapper::new);

        LiveData<ProgramExercise> draftingProgramExercise = repository.getDraftingProgramExercise(programTrainingId);
        loader.addSource(
                draftingProgramExercise,
                ProgramExerciseWrapper::setProgramExercise,
                () -> repository.insertProgramExercise(createProgramExercise(programTrainingId))
        );

        loader.addDependentSource(
                draftingProgramExercise,
                programExercise -> exercisesRepository.getExerciseById(programExercise.getExerciseId()),
                ProgramExerciseWrapper::setExercise
        );

        loader.addDependentSource(
                draftingProgramExercise,
                repository::getProgramSetsForExercise,
                ProgramExerciseWrapper::setProgramSets
        );

        return loader.load();
    }

    public static LiveData<ProgramExerciseWrapper> load(ProgramTrainingRepository repository,
                                                        ExercisesRepository exercisesRepository,
                                                        long programExerciseId) {
        Loader<ProgramExerciseWrapper> loader = new Loader<>(ProgramExerciseWrapper::new);

        LiveData<ProgramExercise> programExercise = repository.getProgramExerciseById(programExerciseId);
        loader.addSource(programExercise, ProgramExerciseWrapper::setProgramExercise);
        loader.addDependentSource(
                programExercise,
                pe -> exercisesRepository.getExerciseById(pe.getExerciseId()),
                ProgramExerciseWrapper::setExercise
        );
        loader.addSource(repository.getProgramSetsForExercise(programExerciseId), ProgramExerciseWrapper::setProgramSets);

        return loader.load();
    }
}
