package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.support.annotation.NonNull;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

public class ProgramTrainingQueryAdapter implements ModelQueryAdapter<Flowable<ProgramTraining>> {
    private ProgramTrainingRepository programTrainingRepository;

    public ProgramTrainingQueryAdapter(@NonNull ProgramTrainingRepository programTrainingRepository) {
        this.programTrainingRepository = programTrainingRepository;
    }

    @NonNull
    @Override
    public Flowable<ProgramTraining> query(long programTrainingId) {
        return programTrainingRepository.getProgramTrainingById(programTrainingId);
    }
}
