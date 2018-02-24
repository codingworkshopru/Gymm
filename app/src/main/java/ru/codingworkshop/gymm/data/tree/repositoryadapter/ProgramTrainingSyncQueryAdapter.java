package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.support.annotation.NonNull;

import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

public class ProgramTrainingSyncQueryAdapter implements ModelQueryAdapter<ProgramTraining> {
    private ProgramTrainingRepository programTrainingRepository;

    public ProgramTrainingSyncQueryAdapter(@NonNull ProgramTrainingRepository programTrainingRepository) {
        this.programTrainingRepository = programTrainingRepository;
    }

    @NonNull
    @Override
    public ProgramTraining query(long id) {
        return programTrainingRepository.getProgramTrainingByIdSync(id);
    }
}
