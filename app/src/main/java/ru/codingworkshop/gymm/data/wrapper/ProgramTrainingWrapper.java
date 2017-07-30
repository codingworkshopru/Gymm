package ru.codingworkshop.gymm.data.wrapper;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Радик on 20.06.2017.
 */

public class ProgramTrainingWrapper {
    private ProgramTraining programTraining;
    private SortableChildrenDelegate<ProgramExercise> childrenDelegate = new SortableChildrenDelegate<>();
    private List<ProgramExerciseWrapper> exerciseWrappers = Lists.newArrayList();

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

    // Wrapped children zone

    public List<ProgramExerciseWrapper> getExerciseWrappers() {
        return exerciseWrappers;
    }

    public void setProgramSets(List<ProgramSet> programSets) {
        Preconditions.checkArgument(!programSets.isEmpty());
        Preconditions.checkState(hasProgramExercises());

        Multimap<Long, ProgramSet> exerciseIdToSets = Multimaps.index(programSets, ProgramSet::getProgramExerciseId);
        for (ProgramExercise exercise : getProgramExercises()) {
            ProgramExerciseWrapper wrapper = new ProgramExerciseWrapper();
            wrapper.setProgramExercise(exercise);
            wrapper.setProgramSets(exerciseIdToSets.get(exercise.getId()));
            exerciseWrappers.add(wrapper);
        }
    }

    private void setExercises(List<Exercise> exercises) {
        Map<Long, Exercise> idExerciseMap = Maps.uniqueIndex(exercises, Exercise::getId);

        for (ProgramExerciseWrapper w : getExerciseWrappers()) {
            w.setExercise(idExerciseMap.get(w.getProgramExercise().getExerciseId()));
        }
    }

    // end of wrapped children zone

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


    public static LiveData<ProgramTrainingWrapper> loadWithWrappedProgramExercises(long programTrainingId, ProgramTrainingRepository repository, ExercisesRepository exercisesRepository) {
        Loader<ProgramTrainingWrapper> loader = new Loader<>(ProgramTrainingWrapper::new);

        loader.addSource(repository.getProgramTrainingById(programTrainingId), ProgramTrainingWrapper::setProgramTraining);

        LiveData<List<ProgramExercise>> programExercisesForTraining = repository.getProgramExercisesForTraining(programTrainingId);
        loader.addSource(programExercisesForTraining, ProgramTrainingWrapper::setProgramExercises);

        LiveData<List<ProgramSet>> liveSets = repository.getProgramSetsForTraining(programTrainingId);
        loader.addDependentSource(programExercisesForTraining, unused -> liveSets, ProgramTrainingWrapper::setProgramSets);

        loader.addDependentSource(liveSets, unused -> exercisesRepository.getExercisesForProgramTraining(programTrainingId), ProgramTrainingWrapper::setExercises);

        return loader.load();
    }
}
