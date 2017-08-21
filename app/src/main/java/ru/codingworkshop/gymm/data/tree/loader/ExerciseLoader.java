package ru.codingworkshop.gymm.data.tree.loader;

import android.support.annotation.NonNull;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.data.tree.node.ExerciseNode;

/**
 * Created by Радик on 18.08.2017 as part of the Gymm project.
 */

public class ExerciseLoader extends NodeLoader<Exercise, MuscleGroup> {
    public ExerciseLoader(@NonNull ExerciseNode node) {
        super(node);
    }

    @Override
    void loadAdditional(SetAndRemove setAndRemove) {}
}
