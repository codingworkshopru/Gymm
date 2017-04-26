package ru.codingworkshop.gymm.ui.program;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import ru.codingworkshop.gymm.BR;
import ru.codingworkshop.gymm.data.model.Orderable;

/**
 * Created by Радик on 26.04.2017.
 */

public class Adapter<B extends ViewDataBinding, M extends Orderable> extends RecyclerView.Adapter<BindingHolder<B>> {
    private List<M> dataList;
    private final @LayoutRes int layout;

    public Adapter(@LayoutRes int layoutRes) {
        layout = layoutRes;
    }

    @Override
    public BindingHolder<B> onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        B binding = DataBindingUtil.inflate(inflater, layout, parent, false);
        return new BindingHolder<>(binding);
    }

    @Override
    public void onBindViewHolder(BindingHolder<B> holder, int position) {
        holder.binding.setVariable(BR.model, dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setDataList(List<M> list) {
        dataList = list;
        notifyDataSetChanged();
    }

    public List<M> getDataList() {
        return dataList;
    }

    public void addModel(@NonNull M model) {
        int index = getItemCount();
        model.setSortOrder(index);
        dataList.add(model);
        notifyItemInserted(index);
    }

    public void replaceModel(@NonNull M model) {
        int index = model.getSortOrder();
        dataList.set(index, model);
        notifyItemChanged(index);
    }

    public void removeModel(@NonNull M model) {
        int index = model.getSortOrder();
        dataList.remove(index);
        notifyItemRemoved(index);
    }
}
