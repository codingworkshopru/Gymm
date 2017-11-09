package ru.codingworkshop.gymm.ui.program.training;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.loader.ProgramTrainingTreeLoader;
import ru.codingworkshop.gymm.data.tree.loader.datasource.ProgramTrainingDataSource;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.saver.ProgramTrainingSaver;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Радик on 20.08.2017 as part of the Gymm project.
 */

public class ProgramTrainingViewModel extends ViewModel {
    private ProgramTrainingRepository repository;
    private ExercisesRepository exercisesRepository;
    private ProgramTrainingTree tree;
    private ProgramTrainingSaver saver;
    private String programTrainingName;
    private boolean childrenChanged;

    @Inject
    public ProgramTrainingViewModel(ProgramTrainingRepository repository, ExercisesRepository exercisesRepository) {
        this.repository = repository;
        this.exercisesRepository = exercisesRepository;
        tree = new MutableProgramTrainingTree();
        saver = new ProgramTrainingSaver(tree, repository);
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
        return Transformations.switchMap(repository.getDraftingProgramTraining(), input -> {
            if (input == null) {
                initTree();
                return LiveDataUtil.getLive(tree);
            } else {
                return load(input.getId());
            }
        });
    }

    public LiveData<ProgramTrainingTree> load(long programTrainingId) {
        ProgramTrainingDataSource dataSource = new ProgramTrainingDataSource(repository, exercisesRepository, programTrainingId);
        ProgramTrainingTreeLoader loader = new ProgramTrainingTreeLoader(tree, dataSource);
        LiveData<ProgramTrainingTree> liveLoaded = loader.loadIt();
        liveLoaded.observeForever(new Observer<ProgramTrainingTree>() {
            @Override
            public void onChanged(@Nullable ProgramTrainingTree loaded) {
                if (loaded != null) {
                    programTrainingName = tree.getParent().getName();
                    liveLoaded.removeObserver(this);
                }
            }
        });
        return liveLoaded;
    }

    public LiveData<Boolean> save() {
        return Transformations.map(repository.getProgramTrainingByName(tree.getParent().getName()), t -> {
            if (t == null) {
                saver.save();
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
