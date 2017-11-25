package ru.codingworkshop.gymm.ui.program.exercise;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.tree.loader.ProgramDraftingExerciseLoader;
import ru.codingworkshop.gymm.data.tree.loader.ProgramExerciseLoader;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.saver.ProgramExerciseSaver;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.db.GymmDatabase;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Радик on 27.08.2017 as part of the Gymm project.
 */

public class ProgramExerciseViewModel extends ViewModel {
    private ProgramExerciseLoader programExerciseLoader;
    private ProgramDraftingExerciseLoader programDraftingExerciseLoader;
    private ProgramTrainingRepository repository;

    @VisibleForTesting
    ProgramExerciseNode node;
    private LiveData<ProgramExerciseNode> liveNode;

    private long programTrainingId;
    private Long oldExerciseId;
    private List<ProgramSet> oldChildren;

    @Inject
    ProgramExerciseViewModel(ProgramExerciseLoader programExerciseLoader, ProgramDraftingExerciseLoader programDraftingExerciseLoader, ProgramTrainingRepository repository) {
        this.programExerciseLoader = programExerciseLoader;
        this.programDraftingExerciseLoader = programDraftingExerciseLoader;
        this.repository = repository;
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

    public LiveData<ProgramExerciseNode> create() {
        if (liveNode == null) {
            Preconditions.checkArgument(GymmDatabase.isValidId(programTrainingId));
            node = new MutableProgramExerciseNode();
            liveNode = Transformations.map(programDraftingExerciseLoader.loadById(node, programTrainingId), loadedNode -> {
                if (loadedNode == null) {
                    initNode();
                }
                return loadedNode;
            });
        }

        return liveNode;
    }

    public LiveData<ProgramExerciseNode> load(long programExerciseId) {
        if (liveNode == null) {
            node = new MutableProgramExerciseNode();
            liveNode = LiveDataUtil.getOnce(
                    programExerciseLoader.loadById(node, programExerciseId),
                    n -> {
                        oldExerciseId = n.getExerciseId();
                        LiveDataUtil.getOnce(repository.getProgramSetsForExercise(n.getId()), sets -> oldChildren = sets);
                    }
            );
        }

        return liveNode;
    }

    public void save() {
        node.getParent().setDrafting(false);
        new ProgramExerciseSaver(node, repository).save();
    }

    public void deleteIfDrafting() {
        final ProgramExercise parent = node.getParent();
        if (parent.isDrafting()) {
            repository.deleteProgramExercise(parent);
        }
    }

    public boolean isChanged() {
        long oldEx = oldExerciseId == null ? 0 : oldExerciseId;
        long newEx = node.getExerciseId() == null ? 0 : node.getExerciseId();

        return oldEx != newEx || !(oldChildren == null && !node.hasChildren()) && !node.getChildren().equals(oldChildren);
    }

    public void addProgramSet(ProgramSet programSet) {
        programSet.setSortOrder(node.getChildren().size());
        node.addChild(programSet);
    }

    public void replaceProgramSet(ProgramSet programSet) {
        node.replaceChild(programSet.getSortOrder(), programSet);
    }
}
