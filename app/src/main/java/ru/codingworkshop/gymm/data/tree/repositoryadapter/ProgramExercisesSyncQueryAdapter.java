package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.support.annotation.NonNull;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

public class ProgramExercisesSyncQueryAdapter implements ModelQueryAdapter<List<ProgramExercise>> {
    private ProgramTrainingRepository programTrainingRepository;

    public ProgramExercisesSyncQueryAdapter(@NonNull ProgramTrainingRepository programTrainingRepository) {
        this.programTrainingRepository = programTrainingRepository;
    }

    @NonNull
    @Override
    public List<ProgramExercise> query(long id) {
        return programTrainingRepository.getProgramExercisesForTrainingSync(id);
    }
}
