package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.support.annotation.NonNull;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

public class ProgramSetsSyncQueryAdapter implements ModelQueryAdapter<List<ProgramSet>> {
    private ProgramTrainingRepository programTrainingRepository;

    public ProgramSetsSyncQueryAdapter(@NonNull ProgramTrainingRepository programTrainingRepository) {
        this.programTrainingRepository = programTrainingRepository;
    }

    @NonNull
    @Override
    public List<ProgramSet> query(long id) {
        return programTrainingRepository.getProgramSetsForTrainingSync(id);
    }
}
