package ru.codingworkshop.gymm.data.tree.loader.common;

import ru.codingworkshop.gymm.data.tree.holder.ChildrenHolder;
import ru.codingworkshop.gymm.data.tree.holder.ParentHolder;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ChildrenAdapter;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ParentAdapter;

/**
 * Created by Radik on 09.11.2017.
 */

public class NodeLoaderDelegate<P, C> extends LoaderDelegate {
    public NodeLoaderDelegate(ParentHolder<P> parentHolder, ChildrenHolder<C> childrenHolder,
                              ParentAdapter<P> parentDataSource, ChildrenAdapter<C> childrenDataSource, long id)

    {
        super();

        addSource(parentDataSource.getParent(id), parentHolder::setParent);
        addSource(childrenDataSource.getChildren(id), childrenHolder::setChildren);
    }
}
