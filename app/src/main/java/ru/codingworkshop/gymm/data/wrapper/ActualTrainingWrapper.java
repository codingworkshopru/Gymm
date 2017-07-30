package ru.codingworkshop.gymm.data.wrapper;

import android.arch.lifecycle.LiveData;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.repository.ActualTrainingRepository;
import ru.codingworkshop.gymm.repository.ExercisesRepository;

/**
 * Created by Радик on 29.07.2017.
 */

public class ActualTrainingWrapper {
    private List<Exercise> exercises;
    private ActualTraining actualTraining;

    public ActualTraining getActualTraining() {
        return actualTraining;
    }

    public void setActualTraining(ActualTraining actualTraining) {
        this.actualTraining = actualTraining;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    public static ActualTraining createActualTraining(long programTrainingId) {
        ActualTraining training = new ActualTraining();
        training.setProgramTrainingId(programTrainingId);
        return training;
    }

    public static LiveData<ActualTrainingWrapper> create(ActualTrainingRepository repository, ExercisesRepository exercisesRepository, long programTrainingId) {
        Loader<ActualTrainingWrapper> loader = new Loader<>(ActualTrainingWrapper::new);
        ActualTraining training = createActualTraining(programTrainingId);

        loader.addSource(exercisesRepository.getExercisesForProgramTraining(programTrainingId), ActualTrainingWrapper::setExercises);
        loader.addDependentSource(repository.insertActualTraining(training), repository::getActualTrainingById, ActualTrainingWrapper::setActualTraining);

        return loader.load();
    }
}
