package ru.codingworkshop.gymm.data.tree.loader;

import javax.inject.Inject;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.tree.loader.builder.ImmutableActualTrainingTreeBuilder;
import ru.codingworkshop.gymm.data.tree.loader.common.Loader;
import ru.codingworkshop.gymm.data.tree.node.ImmutableActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ActualTrainingAdapter;

public class ImmutableActualTrainingTreeLoader implements Loader<ImmutableActualTrainingTree> {
    private ActualTrainingAdapter adapter;

    @Inject
    ImmutableActualTrainingTreeLoader(ActualTrainingAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public Flowable<ImmutableActualTrainingTree> loadById(ImmutableActualTrainingTree tree, long id) {
        ImmutableActualTrainingTreeBuilder actualTrainingTreeBuilder = new ImmutableActualTrainingTreeBuilder(tree);
        return Flowable.zip(
                Flowable.just(actualTrainingTreeBuilder),
                adapter.getParent(id),
                adapter.getChildren(id),
                adapter.getGrandchildren(id),
                (builder, actualTraining, actualExercises, actualSets) -> {
                    builder.setParent(actualTraining);
                    builder.setChildren(actualExercises);
                    builder.setGrandchildren(actualSets);

                    return (ImmutableActualTrainingTree) builder.build();
                });
    }
}
