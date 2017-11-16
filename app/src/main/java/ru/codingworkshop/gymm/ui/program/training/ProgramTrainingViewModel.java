package ru.codingworkshop.gymm.ui.program.training;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.loader.ProgramTrainingTreeLoader;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramTrainingAdapter;
import ru.codingworkshop.gymm.data.tree.saver.ProgramTrainingSaver;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Радик on 20.08.2017 as part of the Gymm project.
 */

public class ProgramTrainingViewModel extends ViewModel {
    private ProgramTrainingTreeLoader loader;
    private ProgramTrainingRepository repository;
    @VisibleForTesting
    ProgramTrainingTree tree;
    private String programTrainingName;
    private boolean childrenChanged;

    @Inject
    public ProgramTrainingViewModel(ProgramTrainingTreeLoader loader, ProgramTrainingRepository repository) {
        this.loader = loader;
        this.repository = repository;
    }

    public ProgramTrainingTree getProgramTrainingTree() {
        return tree;
    }

    private void initTree() {
        tree = new MutableProgramTrainingTree();

        ProgramTraining programTraining = new ProgramTraining();
        programTraining.setDrafting(true); // TODO consider place drafting setter to repository
        repository.insertProgramTraining(programTraining);

        tree.setParent(programTraining);
    }

    public LiveData<ProgramTrainingTree> create() {
        if (tree == null) {
            return Transformations.switchMap(repository.getDraftingProgramTraining(), input -> {
                if (input == null) {
                    initTree();
                    return LiveDataUtil.getLive(tree);
                } else {
                    return load(input.getId());
                }
            });
        } else {
            return LiveDataUtil.getLive(tree);
        }
    }

    public LiveData<ProgramTrainingTree> load(long programTrainingId) {
        if (tree == null) {
            tree = new MutableProgramTrainingTree();
            LiveData<ProgramTrainingTree> liveTree = loader.loadById(tree, programTrainingId);
            return LiveDataUtil.getOnce(liveTree, t -> programTrainingName = t.getParent().getName());
        } else {
            return LiveDataUtil.getLive(tree);
        }
    }

    public LiveData<Boolean> save() {
        return Transformations.map(repository.getProgramTrainingByName(tree.getParent().getName()), t -> {
            if (t == null) {
                new ProgramTrainingSaver(tree, repository).save();
            }
            return t == null;
        });
    }

    public void deleteIfDrafting() {
        final ProgramTraining parent = tree.getParent();
        if (parent.isDrafting()) {
            repository.deleteProgramTraining(parent);
            tree = null;
        }
    }

    public void setChildrenChanged() {
        this.childrenChanged = true;
    }

    public boolean isChanged() {
        return childrenChanged || !tree.getParent().getName().equals(programTrainingName);
    }
}
