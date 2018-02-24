package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

public class ProgramExercisesAlterAdapter implements ModelsAlterAdapter<ProgramExercise> {
    private ProgramTrainingRepository programTrainingRepository;

    public ProgramExercisesAlterAdapter(@NonNull ProgramTrainingRepository programTrainingRepository) {
        this.programTrainingRepository = programTrainingRepository;
    }

    @NonNull
    @Override
    public List<Long> insert(@NonNull Collection<ProgramExercise> models) {
        return programTrainingRepository.insertProgramExercises(models);
    }

    @Override
    public int update(@NonNull Collection<ProgramExercise> models) {
        return programTrainingRepository.updateProgramExercises(models);
    }

    @Override
    public int delete(@NonNull Collection<ProgramExercise> models) {
        return programTrainingRepository.deleteProgramExercises(models);
    }
}
