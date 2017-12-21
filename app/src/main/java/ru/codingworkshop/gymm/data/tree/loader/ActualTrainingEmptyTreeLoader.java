package ru.codingworkshop.gymm.data.tree.loader;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.tree.loader.builder.ActualTrainingTreeBuilder;
import ru.codingworkshop.gymm.data.tree.loader.common.Loader;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;

/**
 * Created by Radik on 16.11.2017.
 */

public class ActualTrainingEmptyTreeLoader implements Loader<ActualTrainingTree> {
    private ProgramTrainingTreeLoader programTrainingTreeLoader;

    @Inject
    ActualTrainingEmptyTreeLoader(@NonNull ProgramTrainingTreeLoader programTrainingTreeLoader) {
        this.programTrainingTreeLoader = programTrainingTreeLoader;
    }

    @Override
    public Flowable<ActualTrainingTree> loadById(ActualTrainingTree tree, long id) {
        ProgramTrainingTree programTrainingTree = new ImmutableProgramTrainingTree();
        return programTrainingTreeLoader.loadById(programTrainingTree, id)
                .map(programTrainingTree1 ->
                        (ActualTrainingTree) new ActualTrainingTreeBuilder(tree)
                        .setProgramTrainingTree(programTrainingTree1)
                        .build()
        );
    }
}
