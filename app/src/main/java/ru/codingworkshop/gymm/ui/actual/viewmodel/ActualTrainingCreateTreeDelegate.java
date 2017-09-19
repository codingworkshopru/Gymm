package ru.codingworkshop.gymm.ui.actual.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.tree.loader.builder.ActualTrainingTreeBuilder;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;

/**
 * Created by Радик on 18.09.2017 as part of the Gymm project.
 */
final class ActualTrainingCreateTreeDelegate extends ActualTrainingTreeDelegate {

    public ActualTrainingCreateTreeDelegate(long id) {
        super(id);
    }

    @Override
    LiveData<Boolean> load(ActualTrainingTree tree) {
        ActualTraining actualTraining = new ActualTraining(id);
        actualTrainingRepository.insertActualTraining(actualTraining);

        return Transformations.map(loadProgramTrainingTree(id), unused -> {
            ActualTrainingTreeBuilder builder = new ActualTrainingTreeBuilder(tree);
            builder.setParent(actualTraining);
            builder.setProgramTrainingTree(programTrainingTree).build();
            return true;
        });
    }
}
