package ru.codingworkshop.gymm.ui.common;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.common.base.Preconditions;

import java.util.List;

/**
 * Created by Радик on 07.10.2017 as part of the Gymm project.
 */

public abstract class ClickableBindingListAdapter<T, V extends ViewDataBinding> extends BindingListAdapter<T, V> {
    private ListItemListeners listeners;

    public ClickableBindingListAdapter(@Nullable List<T> items, @NonNull ListItemListeners listeners) {
        super(items);
        this.listeners = Preconditions.checkNotNull(listeners);
    }

    @Override
    protected V createBinding(ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        V binding = DataBindingUtil.inflate(layoutInflater, listeners.getLayout(), parent, false);
        listeners.setListenersToView(binding.getRoot());
        return binding;
    }
}
