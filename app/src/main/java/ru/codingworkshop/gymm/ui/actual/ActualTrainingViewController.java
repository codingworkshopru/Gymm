package ru.codingworkshop.gymm.ui.actual;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.tree.loader.ProgramTrainingTreeLoader;
import ru.codingworkshop.gymm.data.tree.loader.builder.ActualTrainingTreeBuilder;
import ru.codingworkshop.gymm.data.tree.loader.datasource.ProgramTrainingDataSource;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.repository.ActualTrainingRepository;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Радик on 25.08.2017 as part of the Gymm project.
 */

class ActualTrainingViewController {
    private ActualTrainingTree tree;
    private ActualTrainingRepository actualTrainingRepository;
    private ProgramTrainingRepository programTrainingRepository;
    private ExercisesRepository exercisesRepository;
    private ProgramTrainingTree programTrainingTree;

    @Inject
    public ActualTrainingViewController(ActualTrainingRepository actualTrainingRepository,
                                        ProgramTrainingRepository programTrainingRepository,
                                        ExercisesRepository exercisesRepository
    ) {
        this.actualTrainingRepository = actualTrainingRepository;
        this.programTrainingRepository = programTrainingRepository;
        this.exercisesRepository = exercisesRepository;

        tree = new ActualTrainingTree();
    }

    public LiveData<Boolean> startTraining(long programTrainingId) {
        ActualTraining actualTraining = new ActualTraining(programTrainingId);
        actualTrainingRepository.insertActualTraining(actualTraining);
        tree.setParent(actualTraining);

        LiveData<Boolean> loaded = buildProgramTrainingTree(programTrainingId);
        loaded.observeForever(l -> {
            if (l != null && l) {
                new ActualTrainingTreeBuilder(tree).setProgramTrainingTree(programTrainingTree);
            }
        });

        return loaded;
    }

    @NonNull
    private LiveData<Boolean> buildProgramTrainingTree(long programTrainingId) {
        programTrainingTree = new ImmutableProgramTrainingTree();
        ProgramTrainingDataSource programTrainingDataSource = new ProgramTrainingDataSource(programTrainingRepository, exercisesRepository, programTrainingId);
        return new ProgramTrainingTreeLoader(programTrainingTree, programTrainingDataSource).load();
    }
}
