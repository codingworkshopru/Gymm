package ru.codingworkshop.gymm.data.tree.loader;

import android.support.annotation.NonNull;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.tree.loader.adapter.ActualTrainingTreeAdapter;
import ru.codingworkshop.gymm.data.tree.loader.datasource.ActualTrainingDataSource;

/**
 * Created by Радик on 22.08.2017 as part of the Gymm project.
 */

class ActualTrainingTreeLoader extends TreeLoader<ActualTraining, ActualExercise, ActualSet> {
    public ActualTrainingTreeLoader(@NonNull ActualTrainingTreeAdapter tree, @NonNull ActualTrainingDataSource dataSource) {
        super(tree, dataSource);
    }
}
