package ru.codingworkshop.gymm.data.tree.loader;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.tree.loader.builder.ActualTrainingTreeBuilder;
import ru.codingworkshop.gymm.data.tree.loader.common.Loader;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ActualTrainingAdapter;

/**
 * Created by Радик on 22.08.2017 as part of the Gymm project.
 */

public class ActualTrainingTreeLoader implements Loader<ActualTrainingTree> {
    private ActualTrainingAdapter dataSource;
    private ProgramTrainingTreeLoader programTrainingTreeLoader;

    @Inject
    ActualTrainingTreeLoader(@NonNull ActualTrainingAdapter dataSource, @NonNull ProgramTrainingTreeLoader programTrainingTreeLoader) {
        this.dataSource = dataSource;
        this.programTrainingTreeLoader = programTrainingTreeLoader;
    }

    @Override
    public Flowable<ActualTrainingTree> loadById(ActualTrainingTree tree, long id) {
        return Flowable.just(new ActualTrainingTreeBuilder(tree))
                .map(builder -> {
                    ActualTraining parent = dataSource.getParent(id);
                    builder.setParent(parent);
                    builder.setChildren(dataSource.getChildren(id));
                    builder.setGrandchildren(dataSource.getGrandchildren(id));
                    ProgramTrainingTree programTrainingTree1 = programTrainingTreeLoader.loadById(
                            new ImmutableProgramTrainingTree(),
                            parent.getProgramTrainingId()
                    ).blockingFirst();
                    builder.setProgramTrainingTree(programTrainingTree1);
                    return (ActualTrainingTree) builder.build();
                });
    }
}
