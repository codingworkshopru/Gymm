package ru.codingworkshop.gymm.ui.program.exercise;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.tree.loader.ProgramExerciseLoader;
import ru.codingworkshop.gymm.data.tree.loader.datasource.ProgramExerciseDataSource;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.saver.ProgramExerciseSaver;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Радик on 27.08.2017 as part of the Gymm project.
 */

public class ProgramExerciseViewModel extends ViewModel {
    private ProgramTrainingRepository programTrainingRepository;
    private ExercisesRepository exercisesRepository;
    private ProgramExerciseNode node;

    private long programTrainingId;

    @Inject
    public ProgramExerciseViewModel(ProgramTrainingRepository programTrainingRepository, ExercisesRepository exercisesRepository) {
        this.programTrainingRepository = programTrainingRepository;
        this.exercisesRepository = exercisesRepository;

        node = new MutableProgramExerciseNode();
    }

    public ProgramExerciseNode getProgramExerciseNode() {
        return node;
    }

    public void setProgramTrainingId(long programTrainingId) {
        this.programTrainingId = programTrainingId;
    }

    private void initNode() {
        ProgramExercise programExercise = new ProgramExercise();
        programExercise.setProgramTrainingId(programTrainingId);
        programExercise.setDrafting(true);
        programTrainingRepository.insertProgramExercise(programExercise);

        node.setParent(programExercise);
    }

    public LiveData<Boolean> create() {
        return Transformations.switchMap(programTrainingRepository.getDraftingProgramExercise(programTrainingId), input -> {
            if (input == null) {
                initNode();
                return LiveDataUtil.getLive(true);
            } else {
                return load(input.getId());
            }
        });
    }

    public LiveData<Boolean> load(long programExerciseId) {
        ProgramExerciseDataSource dataSource = new ProgramExerciseDataSource(programTrainingRepository, exercisesRepository, programExerciseId);
        ProgramExerciseLoader loader = new ProgramExerciseLoader(node, dataSource);
        return loader.load();
    }

    public void save() {
        ProgramExerciseSaver saver = new ProgramExerciseSaver(node, programTrainingRepository);
        saver.save();
    }
}