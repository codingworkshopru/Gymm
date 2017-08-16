package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;

/**
 * Created by Радик on 14.08.2017 as part of the Gymm project.
 */
public final class ProgramExerciseLoader extends NodeLoader<ProgramExercise, ProgramSet> {
    private Function<Long, LiveData<Exercise>> liveExerciseGetter;

    public ProgramExerciseLoader(@NonNull ProgramExerciseNode node) {
        super(node);
    }

    public void setLiveExerciseGetter(@NonNull Function<Long, LiveData<Exercise>> liveExerciseGetter) {
        this.liveExerciseGetter = liveExerciseGetter;
    }

    @Override
    void loadAdditional(SetAndRemove setAndRemove) {
        Preconditions.checkNotNull(liveExerciseGetter, "Exercise getter must be not null");
        LiveData<Exercise> liveExercise = Transformations.switchMap(getParent(), pe -> liveExerciseGetter.apply(pe.getExerciseId()));
        ProgramExerciseNode node = (ProgramExerciseNode) getNode();
        setAndRemove.ok(liveExercise, node::setExercise);
    }
}
