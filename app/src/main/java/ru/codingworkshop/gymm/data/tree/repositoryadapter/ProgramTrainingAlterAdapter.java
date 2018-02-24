package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.support.annotation.NonNull;

import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

public class ProgramTrainingAlterAdapter implements SingleModelAlterAdapter<ProgramTraining> {
    private ProgramTrainingRepository programTrainingRepository;

    public ProgramTrainingAlterAdapter(@NonNull ProgramTrainingRepository programTrainingRepository) {
        this.programTrainingRepository = programTrainingRepository;
    }

    @Override
    public long insert(@NonNull ProgramTraining model) {
        return programTrainingRepository.insertProgramTraining(model);
    }

    @Override
    public int update(@NonNull ProgramTraining model) {
        return programTrainingRepository.updateProgramTraining(model);
    }

    @Override
    public int delete(@NonNull ProgramTraining model) {
        return programTrainingRepository.deleteProgramTraining(model);
    }
}
