package ru.codingworkshop.gymm.data.tree;

import org.junit.Test;

import ru.codingworkshop.gymm.data.tree.holder.ImmutableChildrenHolder;
import ru.codingworkshop.gymm.data.tree.holder.SortableRestoreChildrenHolder;
import ru.codingworkshop.gymm.data.tree.node.BaseNode;
import ru.codingworkshop.gymm.util.ModelsFixture;
import ru.codingworkshop.gymm.util.SimpleModel;

import static org.junit.Assert.assertTrue;

/**
 * Created by Радик on 20.08.2017 as part of the Gymm project.
 */

public class ChildRestoreAdapterTest {
    @Test
    public void restoreLastRemoved() {
        BaseNode<Long, SimpleModel> bn = new BaseNode<Long, SimpleModel>(new SortableRestoreChildrenHolder<>(ModelsFixture.createSimpleModels(1L))) {};
        bn.removeChild(0);
        new ChildRestoreAdapter(bn).restoreLastRemoved();
        assertTrue(bn.hasChildren());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void restoreLastRemovedForImmutable() {
        BaseNode<Long, SimpleModel> bn = new BaseNode<Long, SimpleModel>(new ImmutableChildrenHolder<>(ModelsFixture.createSimpleModels(1L))) {};
        new ChildRestoreAdapter(bn).restoreLastRemoved();
    }
}