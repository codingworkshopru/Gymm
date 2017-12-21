package ru.codingworkshop.gymm.ui.actual.viewmodel;

import android.arch.lifecycle.LiveData;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.tree.loader.ProgramTrainingTreeLoader;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramTrainingAdapter;
import ru.codingworkshop.gymm.repository.ActualTrainingRepository;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Радик on 18.09.2017 as part of the Gymm project.
 */
abstract class ActualTrainingTreeDelegate {
    long id;
    ProgramTrainingTree programTrainingTree;
    ActualTrainingRepository actualTrainingRepository;
    private ProgramTrainingRepository programTrainingRepository;
    private ExercisesRepository exercisesRepository;

    public ActualTrainingTreeDelegate(long id) {
        this.id = id;
    }

    public void setActualTrainingRepository(ActualTrainingRepository actualTrainingRepository) {
        this.actualTrainingRepository = actualTrainingRepository;
    }

    public void setProgramTrainingRepository(ProgramTrainingRepository programTrainingRepository) {
        this.programTrainingRepository = programTrainingRepository;
    }

    public void setExercisesRepository(ExercisesRepository exercisesRepository) {
        this.exercisesRepository = exercisesRepository;
    }

    abstract LiveData<ActualTrainingTree> load(ActualTrainingTree tree);

    Flowable<ProgramTrainingTree> loadProgramTrainingTree(long programTrainingId) {
        programTrainingTree = new ImmutableProgramTrainingTree();

        ProgramTrainingAdapter dataSource = new ProgramTrainingAdapter(programTrainingRepository, exercisesRepository);
        ProgramTrainingTreeLoader loader = new ProgramTrainingTreeLoader(dataSource);

        return loader.loadById(programTrainingTree, programTrainingId);
    }
}
