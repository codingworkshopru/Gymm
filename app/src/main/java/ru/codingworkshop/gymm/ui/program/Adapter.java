package ru.codingworkshop.gymm.ui.program;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.google.common.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

import ru.codingworkshop.gymm.data.model.Orderable;
import ru.codingworkshop.gymm.ui.BindingHolder;
import ru.codingworkshop.gymm.ui.program.events.ListEmptinessChangeEvent;

/**
 * Created by Радик on 26.04.2017.
 */

public class Adapter<B extends ViewDataBinding, M extends Orderable> extends RecyclerView.Adapter<BindingHolder<B>> {
    private ViewHolderFactory<B> viewHolderFactory;
    private EventBus eventBus;
    private List<M> dataList;
    private M lastRemoved;

    public Adapter(@NonNull ViewHolderFactory<B> factory, @NonNull EventBus bus) {
        viewHolderFactory = factory;
        eventBus = bus;
    }

    @Override
    public BindingHolder<B> onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewHolderFactory.createViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(BindingHolder<B> holder, int position) {
        holder.binding.setVariable(ru.codingworkshop.gymm.BR.model, dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private void dataSetChanged() {
        notifyDataSetChanged();
        ListEmptinessChangeEvent.post(eventBus, dataList.isEmpty());
    }

    private void itemInserted(int position) {
        notifyItemInserted(position);

        if (dataList.size() == 1)
            ListEmptinessChangeEvent.post(eventBus, false);
    }

    private void itemRemoved(int position) {
        notifyItemRemoved(position);

        if (dataList.size() == 0)
            ListEmptinessChangeEvent.post(eventBus, true);
    }

    public void setDataList(List<M> list) {
        dataList = list;
        dataSetChanged();
    }

    public List<M> getDataList() {
        return dataList;
    }

    public void addModel(@NonNull M model) {
        int index = getItemCount();
        model.setSortOrder(index);
        dataList.add(model);
        itemInserted(index);
    }

    public void replaceModel(@NonNull M model) {
        int index = model.getSortOrder();
        dataList.set(index, model);
        notifyItemChanged(index);
    }

    public void moveModel(int fromPosition, int toPosition) {
        Collections.swap(dataList, fromPosition, toPosition);
        updateSortOrders(Math.min(fromPosition, toPosition), Math.max(fromPosition, toPosition));
        notifyItemMoved(fromPosition, toPosition);
    }

    public void removeModel(@NonNull M model) {
        removeModel(model.getSortOrder());
    }

    public void removeModel(int index) {
        lastRemoved = dataList.remove(index);
        updateSortOrders(index);
        itemRemoved(index);
    }
    public void restoreLastRemoved() {
        int index = lastRemoved.getSortOrder();
        dataList.add(index, lastRemoved);
        updateSortOrders(index);
        itemInserted(index);
    }

    private void updateSortOrders(int start) {
        updateSortOrders(start, getItemCount() - 1);
    }

    private void updateSortOrders(int start, int end) {
        for (int i = start; i <= end; i++)
            dataList.get(i).setSortOrder(i);
    }
}
