package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import javax.inject.Inject;

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
    public ActualTrainingEmptyTreeLoader(@NonNull ProgramTrainingTreeLoader programTrainingTreeLoader) {
        this.programTrainingTreeLoader = programTrainingTreeLoader;
    }

    @Override
    public LiveData<ActualTrainingTree> load(ActualTrainingTree node) {
        return null;
    }

    @Override
    public LiveData<ActualTrainingTree> loadById(ActualTrainingTree tree, long id) {
        return mapProgramTrainingTree(id, programTrainingTree -> {
            return buildActualTrainingTree(tree, programTrainingTree);
        });
    }

    private ActualTrainingTree buildActualTrainingTree(ActualTrainingTree tree, ProgramTrainingTree programTrainingTree) {
        return (ActualTrainingTree) new ActualTrainingTreeBuilder(tree)
                .setProgramTrainingTree(programTrainingTree)
                .build();
    }

    private LiveData<ActualTrainingTree> mapProgramTrainingTree(long programTrainingId, Function<ProgramTrainingTree, ActualTrainingTree> func) {
        ProgramTrainingTree programTrainingTree = new ImmutableProgramTrainingTree();
        return Transformations.map(programTrainingTreeLoader.loadById(programTrainingTree, programTrainingId), func);
    }
}
