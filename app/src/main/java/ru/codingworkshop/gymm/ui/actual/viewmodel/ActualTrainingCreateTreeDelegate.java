package ru.codingworkshop.gymm.ui.actual.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
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
    LiveData<ActualTrainingTree> load(ActualTrainingTree tree) {
        return Transformations.switchMap(loadProgramTrainingTree(id), loaded -> {
            final ProgramTraining programTraining = programTrainingTree.getParent();
            ActualTraining actualTraining = new ActualTraining(programTraining.getId(), programTraining.getName());
            new ActualTrainingTreeBuilder(tree)
                    .setProgramTrainingTree(programTrainingTree)
                    .setParent(actualTraining)
                    .build();
            return Transformations.map(actualTrainingRepository.insertActualTrainingWithResult(actualTraining), id -> tree);
        });
    }
}
