package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

public class ProgramSetsAlterAdapter implements ModelsAlterAdapter<ProgramSet> {
    private ProgramTrainingRepository programTrainingRepository;

    public ProgramSetsAlterAdapter(@NonNull ProgramTrainingRepository programTrainingRepository) {
        this.programTrainingRepository = programTrainingRepository;
    }

    @NonNull
    @Override
    public List<Long> insert(@NonNull Collection<ProgramSet> models) {
        return programTrainingRepository.insertProgramSets(models);
    }

    @Override
    public int update(@NonNull Collection<ProgramSet> models) {
        return programTrainingRepository.updateProgramSets(models);
    }

    @Override
    public int delete(@NonNull Collection<ProgramSet> models) {
        return programTrainingRepository.deleteProgramSets(models);
    }
}
