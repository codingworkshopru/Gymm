package ru.codingworkshop.gymm.ui.program.exercise;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.google.common.base.Preconditions;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.tree.loader.ProgramExerciseLoader;
import ru.codingworkshop.gymm.data.tree.loader.datasource.ProgramExerciseDataSource;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.saver.ProgramExerciseSaver;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.db.GymmDatabase;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Радик on 27.08.2017 as part of the Gymm project.
 */

public class ProgramExerciseViewModel extends ViewModel {
    private ProgramTrainingRepository repository;
    private ExercisesRepository exercisesRepository;
    private ProgramExerciseNode node;

    private long programTrainingId;
    private ProgramExerciseSaver saver;
    private boolean childrenChanged;
    private long exerciseId;

    @Inject
    public ProgramExerciseViewModel(ProgramTrainingRepository repository, ExercisesRepository exercisesRepository) {
        this.repository = repository;
        this.exercisesRepository = exercisesRepository;

        node = new MutableProgramExerciseNode();
        saver = new ProgramExerciseSaver(node, repository);
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
        repository.insertProgramExercise(programExercise);

        node.setParent(programExercise);
    }

    public LiveData<Boolean> create() {
        Preconditions.checkArgument(GymmDatabase.isValidId(programTrainingId));
        LiveData<ProgramExercise> draftingProgramExercise = repository.getDraftingProgramExercise(programTrainingId);
        return Transformations.switchMap(draftingProgramExercise, input -> {
            if (input == null) {
                initNode();
                return LiveDataUtil.getLive(true);
            } else {
                return load(input.getId());
            }
        });
    }

    public LiveData<Boolean> load(long programExerciseId) {
        ProgramExerciseDataSource dataSource = new ProgramExerciseDataSource(repository, exercisesRepository, programExerciseId);
        ProgramExerciseLoader loader = new ProgramExerciseLoader(node, dataSource);
        final LiveData<Boolean> liveLoaded = loader.load();
        liveLoaded.observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean loaded) {
                if (loaded != null && loaded) {
                    exerciseId = node.getParent().getExerciseId();
                    liveLoaded.removeObserver(this);
                }
            }
        });
        return liveLoaded;
    }

    public void save() {
        saver.save();
    }

    public void deleteIfDrafting() {
        final ProgramExercise parent = node.getParent();
        if (parent.isDrafting()) {
            repository.deleteProgramExercise(parent);
            node = null;
        }
    }

    public void setChildrenChanged() {
        this.childrenChanged = true;
    }

    public boolean isChanged() {
        return childrenChanged || node.getParent().getExerciseId() != exerciseId;
    }

    public void addProgramSet(ProgramSet programSet) {
        programSet.setSortOrder(node.getChildren().size());
        node.addChild(programSet);
    }

    public void replaceProgramSet(ProgramSet programSet) {
        node.replaceChild(programSet.getSortOrder(), programSet);
    }
}
