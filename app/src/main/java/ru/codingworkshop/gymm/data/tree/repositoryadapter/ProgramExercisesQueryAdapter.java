package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.support.annotation.NonNull;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

public class ProgramExercisesQueryAdapter implements ModelQueryAdapter<List<ProgramExercise>> {
    private ProgramTrainingRepository programTrainingRepository;

    public ProgramExercisesQueryAdapter(@NonNull ProgramTrainingRepository programTrainingRepository) {
        this.programTrainingRepository = programTrainingRepository;
    }

    @NonNull
    @Override
    public List<ProgramExercise> query(long programTrainingId) {
        return programTrainingRepository.getProgramExercisesForTraining(programTrainingId);
    }
}
