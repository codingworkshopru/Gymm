package ru.codingworkshop.gymm.data.tree.loader;

import android.support.annotation.NonNull;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;

/**
 * Created by Радик on 22.08.2017 as part of the Gymm project.
 */

class ActualTrainingTreeLoader extends TreeLoader<ActualTraining, ActualExercise, ActualSet> {
    public ActualTrainingTreeLoader(@NonNull ActualTrainingTreeAdapter tree) {
        super(tree);
    }
}
