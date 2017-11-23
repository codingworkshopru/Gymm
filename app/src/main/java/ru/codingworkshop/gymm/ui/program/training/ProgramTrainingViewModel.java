package ru.codingworkshop.gymm.ui.program.training;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.loader.ProgramDraftingTrainingTreeLoader;
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
    private ProgramDraftingTrainingTreeLoader draftingLoader;
    private ProgramTrainingRepository repository;
    @VisibleForTesting
    ProgramTrainingTree tree;
    private LiveData<ProgramTrainingTree> liveTree;
    private String programTrainingName;
    private boolean childrenChanged;

    @Inject
    public ProgramTrainingViewModel(ProgramTrainingTreeLoader loader, ProgramDraftingTrainingTreeLoader draftingLoader, ProgramTrainingRepository repository) {
        this.loader = loader;
        this.draftingLoader = draftingLoader;
        this.repository = repository;
    }

    public ProgramTrainingTree getProgramTrainingTree() {
        return tree;
    }

    private void initTree() {
        ProgramTraining programTraining = new ProgramTraining();
        programTraining.setDrafting(true); // TODO consider place drafting setter to repository
        repository.insertProgramTraining(programTraining);

        tree.setParent(programTraining);
    }

    public LiveData<ProgramTrainingTree> create() {
        if (liveTree == null) {
            tree = new MutableProgramTrainingTree();
            liveTree = Transformations.map(draftingLoader.load(tree), loadedTree -> {
                if (loadedTree == null) {
                    initTree();
                }
                return loadedTree;
            });
        }

        return liveTree;
    }

    public LiveData<ProgramTrainingTree> load(long programTrainingId) {
        if (liveTree == null) {
            tree = new MutableProgramTrainingTree();
            liveTree = loader.loadById(tree, programTrainingId);
            LiveDataUtil.getOnce(liveTree, t -> programTrainingName = t.getParent().getName());
        }
        return liveTree;
    }

    public LiveData<Boolean> save() {
        return Transformations.map(repository.getProgramTrainingByName(tree.getParent().getName()), t -> {
            if (t == null) {
                tree.getParent().setDrafting(false);
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
