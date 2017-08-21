package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.node.AbstractProgramTrainingTree;

/**
 * Created by Радик on 16.08.2017 as part of the Gymm project.
 */

public class ProgramTrainingTreeLoader extends NodeLoader<ProgramTraining, ProgramExercise> {
    private LiveData<List<ProgramSet>> liveProgramSets;
    private LiveData<List<Exercise>> liveExercises;

    private ProgramTrainingTreeAdapter treeAdapter;

    private LiveData<Boolean> loaded;

    public ProgramTrainingTreeLoader(@NonNull AbstractProgramTrainingTree tree) {
        super(new ProgramTrainingTreeAdapter(tree));
        treeAdapter = (ProgramTrainingTreeAdapter) getNode();
    }

    @Override
    public LiveData<Boolean> load() {
        loaded = super.load();
        loaded.observeForever(this::buildChildNodes);
        return loaded;
    }

    private void buildChildNodes(boolean isLoaded) {
        if (!isLoaded) return;
        loaded.removeObserver(this::buildChildNodes);
        treeAdapter.build();
    }

    @Override
    void loadAdditional(SetAndRemove setAndRemove) {
        setAndRemove.ok(liveProgramSets, treeAdapter::setProgramSets);
        setAndRemove.ok(liveExercises, treeAdapter::setExercises);
    }

    public void setLiveProgramSets(@NonNull LiveData<List<ProgramSet>> liveProgramSets) {
        Preconditions.checkNotNull(liveProgramSets);
        this.liveProgramSets = liveProgramSets;
    }

    public void setLiveExercises(@NonNull LiveData<List<Exercise>> exercises) {
        Preconditions.checkNotNull(exercises);
        this.liveExercises = exercises;
    }
}