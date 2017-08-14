package ru.codingworkshop.gymm.data.wrapper;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.db.GymmDatabase;
import ru.codingworkshop.gymm.repository.ActualTrainingRepository;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Радик on 08.08.2017 as part of the Gymm project.
 */

class ActualTrainingWrapper {
    private ActualTrainingRepository actualTrainingRepository;
    private ProgramTrainingRepository programTrainingRepository;
    private ExercisesRepository exercisesRepository;

    private ProgramTraining programTraining;
    private List<ProgramExercise> programExercises;
    private ImmutableListMultimap<Long, ProgramSet> programSets;

    private Map<Long, Exercise> exercises;

    private ActualTraining actualTraining;
    private Map<Long, ActualExercise> actualExercises;
    private ListMultimap<Long, ActualSet> actualSets;

    public ActualTrainingWrapper(ActualTrainingRepository actualTrainingRepository, ProgramTrainingRepository programTrainingRepository, ExercisesRepository exercisesRepository) {
        this.actualTrainingRepository = actualTrainingRepository;
        this.programTrainingRepository = programTrainingRepository;
        this.exercisesRepository = exercisesRepository;
    }

    public ProgramTraining getProgramTraining() {
        return programTraining;
    }

    public void setProgramTraining(@NonNull ProgramTraining programTraining) {
        Preconditions.checkNotNull(programTraining, "Trying to set null as program training");
        this.programTraining = programTraining;
    }

    public List<ProgramExercise> getProgramExercises() {
        return programExercises;
    }

    public void setProgramExercises(@NonNull List<ProgramExercise> programExercises) {
        checkCollection(programExercises);
        this.programExercises = programExercises;
    }

    @VisibleForTesting
    void checkCollection(Collection collection) {
        Preconditions.checkNotNull(collection, "Collection is null");
        Preconditions.checkArgument(!collection.isEmpty(), "Collection is empty");
    }

    public List<Exercise> getExercises() {
        return Lists.newArrayList(exercises.values());
    }

    public void setExercises(@NonNull List<Exercise> exercises) {
        checkCollection(exercises);
        this.exercises = Maps.uniqueIndex(exercises, Exercise::getId);
    }

    public List<ProgramSet> getProgramSets() {
        return programSets.values().asList();
    }

    public void setProgramSets(@NonNull List<ProgramSet> programSets) {
        checkCollection(programSets);
        this.programSets = Multimaps.index(programSets, ProgramSet::getProgramExerciseId);
    }

    public List<ProgramSet> getProgramSetsForExercise(@NonNull ProgramExercise exercise) {
        Preconditions.checkNotNull(exercise, "Trying to get sets for null program exercise");
        return getProgramSetsForExercise(exercise.getId());
    }

    public List<ProgramSet> getProgramSetsForExercise(long programExerciseId) {
        return programSets.get(programExerciseId);
    }

    public Exercise getExerciseForProgramExercise(@NonNull ProgramExercise programExercise) {
        Preconditions.checkNotNull(programExercise, "Trying to get exercise for null program exercise");
        return exercises.get(programExercise.getExerciseId());
    }

    public List<ActualExercise> getActualExercises() {
        return Lists.newArrayList(actualExercises.values());
    }

    public void setActualExercises(@NonNull List<ActualExercise> actualExercises) {
        checkCollection(actualExercises);
        checkCollection(programExercises);
        Map<Long, ProgramExercise> programExerciseMap = Maps.uniqueIndex(programExercises, Model::getId);
        this.actualExercises = new TreeMap<>(
            (ex1, ex2) -> programExerciseMap.get(ex1).getSortOrder() - programExerciseMap.get(ex2).getSortOrder()
        );
        this.actualExercises.putAll(Maps.uniqueIndex(actualExercises, ActualExercise::getProgramExerciseId));
    }

    public ActualTraining getActualTraining() {
        return actualTraining;
    }

    public void setActualTraining(@NonNull ActualTraining actualTraining) {
        Preconditions.checkNotNull(actualTraining, "Trying to set null as actual training");
        this.actualTraining = actualTraining;
    }

    public LiveData<ActualTrainingWrapper> create(long programTrainingId) {
        actualTraining = new ActualTraining(programTrainingId, new Date());
        actualTrainingRepository.insertActualTraining(actualTraining);

        Loader<ActualTrainingWrapper> loader = new Loader<>(this);
        loadTrainingProgram(loader, programTrainingId);
        loadExercises(loader, programTrainingId);

        LiveData<ActualTrainingWrapper> liveWrapper = loader.load();

        List<Observer<ActualTrainingWrapper>> containerForObserver = Lists.newArrayListWithCapacity(1);
        Observer<ActualTrainingWrapper> observer = wrapper -> {
            if (wrapper != null) {
                createActualExercises();
                liveWrapper.removeObserver(containerForObserver.get(0));
            }
        };
        containerForObserver.add(observer);
        liveWrapper.observeForever(observer);

        return liveWrapper;
    }

    private void createActualExercises() {
        Preconditions.checkState(programExercises != null && !programExercises.isEmpty());

        List<ActualExercise> actualExercises = Lists.newArrayListWithCapacity(programExercises.size());
        for (ProgramExercise programExercise : programExercises) {
            String exerciseName = getExerciseForProgramExercise(programExercise).getName();
            ActualExercise actualExercise = new ActualExercise(exerciseName, actualTraining.getId(), programExercise.getId());
            actualExercises.add(actualExercise);
        }
        actualTrainingRepository.insertActualExercises(actualExercises);
        setActualExercises(actualExercises);
    }

    private void loadTrainingProgram(Loader<ActualTrainingWrapper> loader, long programTrainingId) {
        loader.addSource(programTrainingRepository.getProgramTrainingById(programTrainingId), ActualTrainingWrapper::setProgramTraining);
        loader.addSource(programTrainingRepository.getProgramExercisesForTraining(programTrainingId), ActualTrainingWrapper::setProgramExercises);
        loader.addSource(programTrainingRepository.getProgramSetsForTraining(programTrainingId), ActualTrainingWrapper::setProgramSets);
    }

    private void loadExercises(Loader<ActualTrainingWrapper> loader, long programTrainingId) {
        loader.addSource(exercisesRepository.getExercisesForProgramTraining(programTrainingId), ActualTrainingWrapper::setExercises);
    }

    public void createActualSet(@NonNull ActualExercise actualExercise, int reps, double weight) {
        checkActualExercise(actualExercise);
        if (actualSets == null) {
            actualSets = newEmptyListMultimap();
        }
        ActualSet actualSet = new ActualSet(actualExercise.getId(), reps);
        actualSet.setWeight(weight);
        actualSets.put(actualExercise.getId(), actualSet);
        actualTrainingRepository.insertActualSet(actualSet);
    }

    @NonNull
    private <T,F> ListMultimap<T, F> newEmptyListMultimap() {
        return Multimaps.newListMultimap(Maps.newHashMap(), Lists::newLinkedList);
    }

    public List<ActualSet> getActualSets(@NonNull ActualExercise actualExercise) {
        checkActualExercise(actualExercise);
        return actualSets.get(actualExercise.getId());
    }

    public void setActualSets(List<ActualSet> actualSets) {
        checkCollection(actualSets);
        this.actualSets = newEmptyListMultimap();
        for (ActualSet set : actualSets) {
            this.actualSets.put(set.getActualExerciseId(), set);
        }
    }

    private void checkActualExercise(@NonNull ActualExercise actualExercise) {
        Preconditions.checkNotNull(actualExercise, "Actual exercise instance is null");
        Preconditions.checkArgument(actualExercise.getId() != GymmDatabase.INVALID_ID, "Actual exercise instance's id is invalid");
    }

    public LiveData<ActualTrainingWrapper> load(long actualTrainingId) {
        final Loader<ActualTrainingWrapper> loader = new Loader<>(this);

        LiveData<ActualTraining> actualTraining = actualTrainingRepository.getActualTrainingById(actualTrainingId);
        loader.addSource(actualTraining, ActualTrainingWrapper::setActualTraining);
        loader.addSource(actualTrainingRepository.getActualSetsForActualTraining(actualTrainingId), ActualTrainingWrapper::setActualSets);
        loader.addDependentSource(actualTraining, at -> programTrainingRepository.getProgramTrainingById(at.getProgramTrainingId()), ActualTrainingWrapper::setProgramTraining);
        loader.addDependentSource(actualTraining, at -> programTrainingRepository.getProgramExercisesForTraining(at.getProgramTrainingId()), (w, programExercises) -> {
            w.setProgramExercises(programExercises);
            loader.addSource(actualTrainingRepository.getActualExercisesForActualTraining(actualTrainingId), ActualTrainingWrapper::setActualExercises);

        });
        loader.addDependentSource(actualTraining, at -> programTrainingRepository.getProgramSetsForTraining(at.getProgramTrainingId()), ActualTrainingWrapper::setProgramSets);

        return loader.load();
    }
}
