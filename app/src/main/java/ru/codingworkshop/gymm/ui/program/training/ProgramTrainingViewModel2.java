package ru.codingworkshop.gymm.ui.program.training;

import android.arch.lifecycle.LiveData;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.loader.NodeLoader;
import ru.codingworkshop.gymm.data.tree.loader.ProgramTrainingTreeLoader;
import ru.codingworkshop.gymm.data.tree.loader.datasource.ProgramTrainingDataSource;
import ru.codingworkshop.gymm.data.tree.node.BaseNode;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.saver.ProgramTrainingSaver;
import ru.codingworkshop.gymm.data.tree.saver.Saver;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.ui.program.BaseVM;

/**
 * Created by Радик on 08.10.2017 as part of the Gymm project.
 */

public class ProgramTrainingViewModel2 extends BaseVM<ProgramTraining> {
    private ProgramTrainingRepository repository;
    private ExercisesRepository exercisesRepository;
    private ProgramTrainingTree tree;

    @Inject
    public ProgramTrainingViewModel2(ProgramTrainingRepository repository, ExercisesRepository exercisesRepository) {
        this.repository = repository;
        this.exercisesRepository = exercisesRepository;
        tree = new ImmutableProgramTrainingTree();
    }

    @Override
    protected void insertParent(ProgramTraining parent) {
        repository.insertProgramTraining(parent);
    }

    @Override
    protected BaseNode<ProgramTraining, ?> getNode() {
        return tree;
    }

    @Override
    protected ProgramTraining createParent() {
        return new ProgramTraining();
    }

    @Override
    protected LiveData<ProgramTraining> getDrafting() {
        return repository.getDraftingProgramTraining();
    }

    @Override
    protected NodeLoader<?, ?> getLoader(long programTrainingId) {
        ProgramTrainingDataSource dataSource = new ProgramTrainingDataSource(repository, exercisesRepository, programTrainingId);
        return new ProgramTrainingTreeLoader(tree, dataSource);
    }

    @Override
    protected Saver getSaver() {
        return new ProgramTrainingSaver(tree, repository);
    }
}
