package ru.codingworkshop.gymm.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Radik on 22.11.2017.
 */

public class MainActivityViewModel extends ViewModel {
    private ProgramTrainingRepository programTrainingRepository;
    private LiveData<List<ProgramTraining>> programTrainings;

    @Inject
    MainActivityViewModel(ProgramTrainingRepository programTrainingRepository) {
        this.programTrainingRepository = programTrainingRepository;
    }

    public LiveData<List<ProgramTraining>> load() {
        if (programTrainings == null) {
            programTrainings = programTrainingRepository.getProgramTrainings();
        }
        return programTrainings;
    }
}
