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
        return Flowable.just(new ImmutableActualTrainingTreeBuilder(tree))
                .map(builder -> {
                    builder.setParent(adapter.getParent(id));
                    builder.setChildren(adapter.getChildren(id));
                    builder.setGrandchildren(adapter.getGrandchildren(id));
                    return (ImmutableActualTrainingTree) builder.build();
                });
    }
}
