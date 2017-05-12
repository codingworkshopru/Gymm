package ru.codingworkshop.gymm.ui.program;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.google.common.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

import io.requery.Persistable;
import ru.codingworkshop.gymm.data.model.Draftable;
import ru.codingworkshop.gymm.data.model.Orderable;
import ru.codingworkshop.gymm.ui.BindingHolder;
import ru.codingworkshop.gymm.ui.program.event.ListEmptinessChangeEvent;

/**
 * Created by Радик on 26.04.2017.
 */

public class Adapter<B extends ViewDataBinding, M extends Orderable & Persistable & Draftable> extends RecyclerView.Adapter<BindingHolder<B>> {
    private ViewHolderFactory<B> viewHolderFactory;
    private EventBus eventBus;
    private ModelHolder<?, M> modelHolder;
    private M lastRemoved;

    public Adapter(@NonNull ViewHolderFactory<B> factory, @NonNull EventBus bus) {
        viewHolderFactory = factory;
        eventBus = bus;
    }

    private List<M> list() {
        return modelHolder.getChildren();
    }

    @Override
    public BindingHolder<B> onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewHolderFactory.createViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(BindingHolder<B> holder, int position) {
        holder.binding.setVariable(ru.codingworkshop.gymm.BR.model, list().get(position));
    }

    @Override
    public int getItemCount() {
        return list().size();
    }

    private void dataSetChanged() {
        notifyDataSetChanged();
        ListEmptinessChangeEvent.post(eventBus, list().isEmpty());
    }

    private void itemInserted(int position) {
        notifyItemInserted(position);

        if (list().size() == 1)
            ListEmptinessChangeEvent.post(eventBus, false);
    }

    private void itemRemoved(int position) {
        notifyItemRemoved(position);

        if (list().size() == 0)
            ListEmptinessChangeEvent.post(eventBus, true);
    }

    public void setModelHolder(ModelHolder<?, M> holder) {
        modelHolder = holder;
        dataSetChanged();
    }

    public void addModel(@NonNull M model) {
        int index = getItemCount();
        model.setSortOrder(index);
        list().add(model);
        modelHolder.addNewChild(model);
        itemInserted(index);
    }

    public void replaceModel(@NonNull M model) {
        int index = model.getSortOrder();
        list().set(index, model);
        modelHolder.replaceChild(model);
        notifyItemChanged(index);
    }

    public void moveModel(int fromPosition, int toPosition) {
        Collections.swap(list(), fromPosition, toPosition);
        updateSortOrders(Math.min(fromPosition, toPosition), Math.max(fromPosition, toPosition));
        notifyItemMoved(fromPosition, toPosition);
    }

    public void removeModel(@NonNull M model) {
        removeModel(model.getSortOrder());
    }

    public void removeModel(int index) {
        lastRemoved = list().remove(index);
        updateSortOrders(index);
        itemRemoved(index);
    }

    public void restoreLastRemoved() {
        int index = lastRemoved.getSortOrder();
        list().add(index, lastRemoved);
        updateSortOrders(index);
        itemInserted(index);
    }

    private void updateSortOrders(int start) {
        updateSortOrders(start, getItemCount() - 1);
    }

    private void updateSortOrders(int start, int end) {
        List<M> list = list();
        for (int i = start; i <= end; i++)
            list.get(i).setSortOrder(i);
    }
}
