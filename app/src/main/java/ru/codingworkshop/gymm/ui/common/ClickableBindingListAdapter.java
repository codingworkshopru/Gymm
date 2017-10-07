package ru.codingworkshop.gymm.ui.common;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Радик on 07.10.2017 as part of the Gymm project.
 */

public abstract class ClickableBindingListAdapter<T, V extends ViewDataBinding> extends BindingListAdapter<T, V> {
    private Context context;
    private @LayoutRes int listItem;
    private List<Observer<View>> itemClickObservers;


    public ClickableBindingListAdapter(Context context, @LayoutRes int listItem, @Nullable List<T> items) {
        super(items);
        this.context = context;
        this.listItem = listItem;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected V createBinding(ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        V binding = DataBindingUtil.inflate(layoutInflater, listItem, parent, false);
        binding.getRoot().setOnClickListener(this::notifyOnClickObservers);
        return binding;
    }

    public void addOnClickObserver(Observer<View> observer) {
        if (itemClickObservers == null) {
            itemClickObservers = new LinkedList<>();
        }
        itemClickObservers.add(observer);
    }

    public void removeOnClickObserver(Observer<View> observer) {
        if (hasItemClickObservers()) {
            itemClickObservers.remove(observer);
        }
    }

    private boolean hasItemClickObservers() {
        return itemClickObservers != null && !itemClickObservers.isEmpty();
    }

    private void notifyOnClickObservers(View binding) {
        if (hasItemClickObservers()) {
            for (Observer<View> o : itemClickObservers) {
                o.onChanged(binding);
            }
        }
    }
}
