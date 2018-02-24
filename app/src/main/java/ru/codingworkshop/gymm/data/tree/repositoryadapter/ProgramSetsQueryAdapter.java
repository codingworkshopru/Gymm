package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

public class ProgramSetsQueryAdapter implements ModelQueryAdapter<Flowable<List<ProgramSet>>> {
    private ProgramTrainingRepository programTrainingRepository;

    public ProgramSetsQueryAdapter(@NonNull ProgramTrainingRepository programTrainingRepository) {
        this.programTrainingRepository = programTrainingRepository;
    }

    @NonNull
    @Override
    public Flowable<List<ProgramSet>> query(long programTrainingId) {
        return programTrainingRepository.getProgramSetsForTraining(programTrainingId);
    }
}
