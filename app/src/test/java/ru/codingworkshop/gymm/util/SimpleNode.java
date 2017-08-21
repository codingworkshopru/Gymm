package ru.codingworkshop.gymm.util;

import ru.codingworkshop.gymm.data.tree.holder.SimpleChildrenHolder;
import ru.codingworkshop.gymm.data.tree.node.BaseNode;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */

public class SimpleNode extends BaseNode<Long, String> {
    private Integer additional;

    public SimpleNode() {
        super(new SimpleChildrenHolder<>());
    }

    public Integer getAdditional() {
        return additional;
    }

    public void setAdditional(Integer additional) {
        this.additional = additional;
    }
}
