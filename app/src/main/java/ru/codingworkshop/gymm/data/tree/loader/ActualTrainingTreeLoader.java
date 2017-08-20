package ru.codingworkshop.gymm.data.tree.loader;

import android.support.annotation.NonNull;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.tree.node.BaseNode;

/**
 * Created by Радик on 18.08.2017 as part of the Gymm project.
 */

public class ActualTrainingTreeLoader extends NodeLoader<ActualTraining, ActualExercise> {
    public ActualTrainingTreeLoader(@NonNull BaseNode<ActualTraining, ActualExercise> node, ProgramTrainingTreeLoader programTrainingTreeLoader) {
        super(node);
    }
}
