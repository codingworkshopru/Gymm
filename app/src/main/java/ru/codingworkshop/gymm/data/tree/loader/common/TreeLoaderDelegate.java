package ru.codingworkshop.gymm.data.tree.loader.common;

import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.tree.loader.builder.TreeBuilder;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ChildrenAdapter;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.GrandchildrenAdapter;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ParentAdapter;

/**
 * Created by Radik on 09.11.2017.
 */

public class TreeLoaderDelegate<P, C extends Model, GC> extends NodeLoaderDelegate<P, C> {
    private TreeBuilder<P, C, GC> treeBuilder;

    public TreeLoaderDelegate(TreeBuilder<P, C, GC> treeBuilder, ParentAdapter<P> parentDataSource,
                              ChildrenAdapter<C> childrenDataSource,
                              GrandchildrenAdapter<GC> grandchildrenDataSource, long id)
    {
        super(treeBuilder, treeBuilder, parentDataSource, childrenDataSource, id);
        this.treeBuilder = treeBuilder;
        addSource(grandchildrenDataSource.getGrandchildren(id), treeBuilder::setGrandchildren);
    }

    @Override
    protected void onAllSourcesLoaded() {
        treeBuilder.build();
        super.onAllSourcesLoaded();
    }
}
