package ru.codingworkshop.gymm.data.wrapper;

import android.arch.lifecycle.LiveData;

import com.google.common.base.Function;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.common.Sortable;
import ru.codingworkshop.gymm.data.util.BiConsumer;

/**
 * Created by Радик on 06.08.2017 as part of the Gymm project.
 */

public abstract class BaseLoader<W extends BaseWrapper<P, C>, P, C extends Sortable> {
    private Loader<W> loader;
    private LiveData<P> root;

    public BaseLoader(W wrapper) {
        loader = new Loader<>(wrapper);
    }

    public LiveData<W> load() {
        if (root == null) {
            addSources();
        }

        return loader.load();
    }

    protected void addSources() {
        addRoot();
        addChildren();
    }

    private void addRoot() {
        loader.addSource(getRoot(), BaseWrapper::setRoot, this::runIfRootAbsent);
    }

    private LiveData<P> getRoot() {
        if (root == null) {
            root = getLiveRoot();
        }

        return root;
    }

    protected abstract LiveData<P> getLiveRoot();

    protected void runIfRootAbsent() {}

    private void addChildren() {
        LiveData<List<C>> children = getLiveChildren();
        if (children != null) {
            loader.addSource(children, BaseWrapper::setChildren);
        } else {
            loader.addDependentSource(getRoot(), this::getLiveChildren, BaseWrapper::setChildren);
        }
    }

    protected LiveData<List<C>> getLiveChildren() {
        return null;
    }

    protected LiveData<List<C>> getLiveChildren(P root) {
        return null;
    }

    protected <T> void addSource(LiveData<T> source, BiConsumer<W, T> resultSetter) {
        loader.addSource(source, resultSetter);
    }

    protected <T> void addDependentSource(Function<P, LiveData<T>> sourceGetter, BiConsumer<W, T> resultSetter) {
        loader.addDependentSource(getRoot(), sourceGetter::apply, resultSetter);
    }
}
