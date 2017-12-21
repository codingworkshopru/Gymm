package ru.codingworkshop.gymm.ui.actual.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;

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
        return LiveDataReactiveStreams.fromPublisher(
                loadProgramTrainingTree(id)
                        .map(programTrainingTree1 -> {
                            ActualTrainingTree builtTree = (ActualTrainingTree) new ActualTrainingTreeBuilder(tree)
                                    .setProgramTrainingTree(programTrainingTree)
                                    .build();

                            actualTrainingRepository.insertActualTraining(tree.getParent());

                            return builtTree;
                        })
        );
    }
}
