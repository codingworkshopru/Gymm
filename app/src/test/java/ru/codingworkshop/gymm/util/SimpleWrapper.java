package ru.codingworkshop.gymm.util;

import ru.codingworkshop.gymm.data.wrapper.BaseWrapper;

/**
 * Created by Радик on 31.07.2017 as part of the Gymm project.
 */
public final class SimpleWrapper extends BaseWrapper<Long, SimpleModel> {
    private Long number;
    private String string;
    private Boolean bool;

    public SimpleWrapper() {
    }

    @Override
    protected void saveRoot() {

    }

    @Override
    protected void saveChildren() {

    }

    public void setBool(Boolean bool) {
        this.bool = bool;
    }

    public Boolean getBool() {
        return bool;
    }
}
