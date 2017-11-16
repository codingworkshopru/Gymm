package ru.codingworkshop.gymm.ui.actual.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.loader.builder.ActualTrainingTreeBuilder;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;

/**
 * Created by Радик on 18.09.2017 as part of the Gymm project.
 */
final class ActualTrainingCreateTreeDelegate extends ActualTrainingTreeDelegate {

    public ActualTrainingCreateTreeDelegate(long id) {
        super(id);
    }

    @Override
    LiveData<ActualTrainingTree> load(ActualTrainingTree tree) {
        return Transformations.switchMap(loadProgramTrainingTree(id), (ProgramTrainingTree loaded) -> {
            new ActualTrainingTreeBuilder(tree)
                    .setProgramTrainingTree(programTrainingTree)
                    .build();

            return Transformations.map(actualTrainingRepository.insertActualTrainingWithResult(tree.getParent()), id -> tree);
        });
    }
}
