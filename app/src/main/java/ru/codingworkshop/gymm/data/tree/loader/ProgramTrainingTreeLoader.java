package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExerciseInterface;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;

/**
 * Created by Радик on 16.08.2017 as part of the Gymm project.
 */

public class ProgramTrainingTreeLoader extends NodeLoader<ProgramTraining, ProgramExerciseInterface> {
    private LiveData<List<ProgramSet>> liveProgramSets;
    private LiveData<List<Exercise>> liveExercises;

    private Multimap<Long, ProgramSet> programSetsMultimap; // can be moved to tree itself with setters
    private Map<Long, Exercise> exercisesMap; // save as multimap with sets

    public ProgramTrainingTreeLoader(@NonNull ProgramTrainingTree node) {
        super(node);
    }

    @Override
    public LiveData<Boolean> load() {
        LiveData<Boolean> loaded = super.load();

        loaded.observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                buildChildNodes();
                loaded.removeObserver(this);
            }
        });

        return loaded;
    }

    private void buildChildNodes() {
        for (ProgramExerciseInterface node : getNode().getChildren()) {
            ProgramExerciseNode castedNode = (ProgramExerciseNode) node;
            castedNode.setChildren(new ArrayList<>(programSetsMultimap.get(node.getId())));
            castedNode.setExercise(exercisesMap.get(node.getExerciseId()));
        }
    }

    @Override
    void loadAdditional(SetAndRemove setAndRemove) {
        setAndRemove.ok(liveProgramSets, this::setProgramSets);
        setAndRemove.ok(liveExercises, this::setExercises);
    }

    public void setLiveProgramSets(@NonNull LiveData<List<ProgramSet>> liveProgramSets) {
        Preconditions.checkNotNull(liveProgramSets);
        this.liveProgramSets = liveProgramSets;
    }

    public void setLiveExercises(@NonNull LiveData<List<Exercise>> exercises) {
        Preconditions.checkNotNull(exercises);
        this.liveExercises = exercises;
    }

    private void setProgramSets(List<ProgramSet> programSets) {
        this.programSetsMultimap = Multimaps.index(programSets, ProgramSet::getProgramExerciseId);
    }

    private void setExercises(List<Exercise> exercises) {
        this.exercisesMap = Maps.uniqueIndex(exercises, Exercise::getId);
    }
}