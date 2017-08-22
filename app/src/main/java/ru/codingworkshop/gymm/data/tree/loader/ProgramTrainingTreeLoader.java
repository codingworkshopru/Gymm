package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;

/**
 * Created by Радик on 16.08.2017 as part of the Gymm project.
 */

public class ProgramTrainingTreeLoader extends TreeLoader<ProgramTraining, ProgramExercise, ProgramSet> {
    private LiveData<List<Exercise>> liveExercises;
    private ProgramTrainingTreeAdapter treeAdapter;

    public ProgramTrainingTreeLoader(@NonNull ProgramTrainingTree tree) {
        super(new ProgramTrainingTreeAdapter(tree));
        treeAdapter = (ProgramTrainingTreeAdapter) getNode();
    }

    @Override
    void loadAdditional(SetAndRemove setAndRemove) {
        super.loadAdditional(setAndRemove);
        setAndRemove.ok(liveExercises, treeAdapter::setExercises);
    }

    public void setLiveExercises(@NonNull LiveData<List<Exercise>> exercises) {
        this.liveExercises = Preconditions.checkNotNull(exercises);
    }
}