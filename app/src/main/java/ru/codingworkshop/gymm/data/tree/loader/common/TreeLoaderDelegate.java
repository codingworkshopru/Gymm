package ru.codingworkshop.gymm.data.tree.loader.common;

import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.tree.loader.builder.TreeBuilder;
import ru.codingworkshop.gymm.data.tree.loader.datasource.TreeDataSource;

/**
 * Created by Radik on 09.11.2017.
 */

public class TreeLoaderDelegate<P, C extends Model, GC> extends LoaderDelegate {
    private TreeBuilder<P, C, GC> treeBuilder;

    public TreeLoaderDelegate(TreeDataSource<P, C, GC> dataSource, TreeBuilder<P, C, GC> treeBuilder) {
        super();
        this.treeBuilder = treeBuilder;

        addSource(dataSource.getParent(), treeBuilder::setParent);
        addSource(dataSource.getChildren(), treeBuilder::setChildren);
        addSource(dataSource.getGrandchildren(), treeBuilder::setGrandchildren);
    }

    @Override
    protected void onAllSourcesLoaded() {
        treeBuilder.build();
        super.onAllSourcesLoaded();
    }
}
