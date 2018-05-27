package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.support.annotation.NonNull;

import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

public class ProgramTrainingQueryAdapter implements ModelQueryAdapter<ProgramTraining> {
    private ProgramTrainingRepository programTrainingRepository;

    public ProgramTrainingQueryAdapter(@NonNull ProgramTrainingRepository programTrainingRepository) {
        this.programTrainingRepository = programTrainingRepository;
    }

    @NonNull
    @Override
    public ProgramTraining query(long programTrainingId) {
        return programTrainingRepository.getProgramTrainingById(programTrainingId);
    }
}
