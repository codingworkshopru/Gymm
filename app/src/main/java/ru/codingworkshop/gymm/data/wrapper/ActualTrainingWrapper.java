package ru.codingworkshop.gymm.data.wrapper;

import android.arch.lifecycle.LiveData;

import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.repository.ActualTrainingRepository;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Радик on 29.07.2017.
 */

public class ActualTrainingWrapper {
    private ActualTraining actualTraining;
    private ProgramTrainingWrapper programTrainingWrapper;

    public ActualTraining getActualTraining() {
        return actualTraining;
    }

    public void setActualTraining(ActualTraining actualTraining) {
        this.actualTraining = actualTraining;
    }

    public ProgramTrainingWrapper getProgramTrainingWrapper() {
        return programTrainingWrapper;
    }

    public void setProgramTrainingWrapper(ProgramTrainingWrapper programTrainingWrapper) {
        this.programTrainingWrapper = programTrainingWrapper;
    }

    public static ActualTraining createActualTraining(long programTrainingId) {
        ActualTraining training = new ActualTraining();
        training.setProgramTrainingId(programTrainingId);
        return training;
    }

    public static LiveData<ActualTrainingWrapper> create(
            long programTrainingId,
            ActualTrainingRepository repository,
            ProgramTrainingRepository programTrainingRepository,
            ExercisesRepository exercisesRepository) {
        Loader<ActualTrainingWrapper> loader = new Loader<>(ActualTrainingWrapper::new);

        loader.addSource(
                ProgramTrainingWrapper.loadWithWrappedProgramExercises(programTrainingId, programTrainingRepository, exercisesRepository),
                ActualTrainingWrapper::setProgramTrainingWrapper
        );
        ActualTraining training = createActualTraining(programTrainingId);
        loader.addDependentSource(repository.insertActualTraining(training), repository::getActualTrainingById, ActualTrainingWrapper::setActualTraining);

        return loader.load();
    }

    public static LiveData<ActualTrainingWrapper> load(long actualTrainingId, ActualTrainingRepository actualTrainingRepository) {
        Loader<ActualTrainingWrapper> loader = new Loader<>(ActualTrainingWrapper::new);
        loader.addSource(actualTrainingRepository.getActualTrainingById(actualTrainingId), ActualTrainingWrapper::setActualTraining);
        return loader.load();
    }
}
