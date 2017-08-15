package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Радик on 14.08.2017 as part of the Gymm project.
 */
public final class ProgramExerciseLoader {
    private ProgramTrainingRepository repository;
    private ExercisesRepository exercisesRepository;

    public ProgramExerciseLoader(ProgramTrainingRepository repository, ExercisesRepository exercisesRepository) {
        this.repository = repository;
        this.exercisesRepository = exercisesRepository;
    }

    public LiveData<Boolean> loadToNode(ProgramExerciseNode node, long programExerciseId) {
        SetAndRemove setAndRemove = new SetAndRemove(3);

        final LiveData<ProgramExercise> programExerciseById = repository.getProgramExerciseById(programExerciseId);
        final LiveData<List<ProgramSet>> programSetsForExercise = repository.getProgramSetsForExercise(programExerciseId);
        final LiveData<Exercise> yLiveData = Transformations.switchMap(programExerciseById, pe -> exercisesRepository.getExerciseById(pe.getExerciseId()));

        setAndRemove.ok(programExerciseById, node::setParent);
        setAndRemove.ok(programSetsForExercise, node::setChildren);
        setAndRemove.ok(yLiveData, node::setExercise);

        return setAndRemove.getLoaded();
    }
}
