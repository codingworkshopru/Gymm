package ru.codingworkshop.gymm.ui.program.training;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.tree.loader.ProgramTrainingTreeLoader;
import ru.codingworkshop.gymm.data.tree.node.AbstractProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramTrainingTree;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Радик on 20.08.2017 as part of the Gymm project.
 */

class ProgramTrainingViewModel extends ViewModel {
    private ProgramTrainingRepository repository;
    private ExercisesRepository exercisesRepository;

    @Inject
    public ProgramTrainingViewModel(ProgramTrainingRepository repository, ExercisesRepository exercisesRepository) {
        this.repository = repository;
        this.exercisesRepository = exercisesRepository;
    }

    public LiveData<Boolean> init(long programTrainingId) {
        AbstractProgramTrainingTree tree = new MutableProgramTrainingTree();

        ProgramTrainingTreeLoader loader = new ProgramTrainingTreeLoader(tree);
        loader.setParent(repository.getProgramTrainingById(programTrainingId));
        loader.setChildren(repository.getProgramExercisesForTraining(programTrainingId));
        loader.setLiveProgramSets(repository.getProgramSetsForTraining(programTrainingId));
        loader.setLiveExercises(exercisesRepository.getExercisesForProgramTraining(programTrainingId));

        return loader.load();
    }
}
