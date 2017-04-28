package ru.codingworkshop.gymm.ui.program;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.eventbus.EventBus;

import ru.codingworkshop.gymm.ui.program.events.ClickViewEvent;
import ru.codingworkshop.gymm.ui.program.events.LongClickViewEvent;
import ru.codingworkshop.gymm.ui.program.events.TouchViewEvent;

/**
 * Created by Радик on 28.04.2017.
 */

public class ViewHolderFactory<B extends ViewDataBinding> {
    private EventBus eventBus;
    private
    @LayoutRes
    int layoutId;
    private
    @IdRes
    int dragItemId;

    public ViewHolderFactory(@NonNull EventBus bus, @LayoutRes int layout, @IdRes int dragItem) {
        eventBus = bus;
        layoutId = layout;
        dragItemId = dragItem;
    }

    public BindingHolder<B> createViewHolder(@NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final B binding = DataBindingUtil.inflate(inflater, layoutId, parent, false);
        BindingHolder<B> viewHolder = new BindingHolder<>(binding);

        View root = binding.getRoot();
        root.setOnClickListener(ClickViewEvent.createClickEvent(eventBus, binding));
        root.setOnLongClickListener(LongClickViewEvent.createLongClickEvent(eventBus, binding));
        root.findViewById(dragItemId).setOnTouchListener(TouchViewEvent.createTouchEvent(eventBus, viewHolder));
        return viewHolder;
    }
}
