package ru.codingworkshop.gymm.data.tree.loader.common;

import ru.codingworkshop.gymm.data.tree.loader.datasource.NodeDataSource;
import ru.codingworkshop.gymm.data.tree.node.BaseNode;

/**
 * Created by Radik on 09.11.2017.
 */

public class BaseNodeLoaderDelegate<P, C> extends LoaderDelegate {
    public BaseNodeLoaderDelegate(BaseNode<P, C> node, NodeDataSource<P, C> dataSource) {
        super();

        addSource(dataSource.getParent(), node::setParent);
        addSource(dataSource.getChildren(), node::setChildren);
    }
}
