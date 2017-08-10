package ru.codingworkshop.gymm.data.wrapper;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;

import java.util.Date;
import java.util.List;
import java.util.Map;

import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
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
        Preconditions.checkNotNull(programExercises, "Trying to set null as program exercises");
        Preconditions.checkArgument(!programExercises.isEmpty(), "Trying to set empty program exercises");
        this.programExercises = programExercises;
    }

    public List<Exercise> getExercises() {
        return Lists.newArrayList(exercises.values());
    }

    public void setExercises(@NonNull List<Exercise> exercises) {
        Preconditions.checkNotNull(exercises, "Trying to set null as exercises");
        Preconditions.checkArgument(!exercises.isEmpty(), "Trying to set empty exercises");
        this.exercises = Maps.uniqueIndex(exercises, Exercise::getId);
    }

    public List<ProgramSet> getProgramSets() {
        return programSets.values().asList();
    }

    public void setProgramSets(@NonNull List<ProgramSet> programSets) {
        Preconditions.checkNotNull(programSets, "Trying to set null as program sets");
        Preconditions.checkArgument(!programSets.isEmpty(), "Trying to set empty program sets");
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

    public LiveData<ActualTrainingWrapper> load(long programTrainingId) {
        Loader<ActualTrainingWrapper> loader = new Loader<>(this);

        loadTrainingProgram(loader, programTrainingId);
        loadExercises(loader, programTrainingId);

        return loader.load();
    }

    private void loadTrainingProgram(Loader<ActualTrainingWrapper> loader, long programTrainingId) {
        loader.addSource(programTrainingRepository.getProgramTrainingById(programTrainingId), ActualTrainingWrapper::setProgramTraining);
        loader.addSource(programTrainingRepository.getProgramExercisesForTraining(programTrainingId), ActualTrainingWrapper::setProgramExercises);
        loader.addSource(programTrainingRepository.getProgramSetsForTraining(programTrainingId), ActualTrainingWrapper::setProgramSets);
    }

    private void loadExercises(Loader<ActualTrainingWrapper> loader, long programTrainingId) {
        loader.addSource(exercisesRepository.getExercisesForProgramTraining(programTrainingId), ActualTrainingWrapper::setExercises);
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

        Loader<ActualTrainingWrapper> loader = new Loader<>(this);
        actualTrainingRepository.insertActualTraining(actualTraining);

        loadTrainingProgram(loader, programTrainingId);

        loadExercises(loader, programTrainingId);

        return loader.load();
    }
}
