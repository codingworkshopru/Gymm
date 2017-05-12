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

import ru.codingworkshop.gymm.BR;
import ru.codingworkshop.gymm.ui.BindingHolder;
import ru.codingworkshop.gymm.ui.program.event.ClickViewEvent;
import ru.codingworkshop.gymm.ui.program.event.LongClickViewEvent;
import ru.codingworkshop.gymm.ui.program.event.TouchViewEvent;

/**
 * Created by Радик on 28.04.2017.
 */

public class ViewHolderFactory<B extends ViewDataBinding> {
    private EventBus eventBus;
    private ActivityProperties activityProperties;
    private @LayoutRes int layoutId;
    private @IdRes int dragItemId;

    public ViewHolderFactory(@NonNull EventBus bus, @NonNull ActivityProperties properties, @LayoutRes int layout, @IdRes int dragItem) {
        eventBus = bus;
        activityProperties = properties;
        layoutId = layout;
        dragItemId = dragItem;
    }

    public BindingHolder<B> createViewHolder(@NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final B binding = DataBindingUtil.inflate(inflater, layoutId, parent, false);
        binding.setVariable(BR.properties, activityProperties);
        BindingHolder<B> viewHolder = new BindingHolder<>(binding);

        View root = binding.getRoot();
        root.setOnClickListener(ClickViewEvent.createClickEvent(eventBus, binding));
        root.setOnLongClickListener(LongClickViewEvent.createLongClickEvent(eventBus, binding));
        root.findViewById(dragItemId).setOnTouchListener(TouchViewEvent.createTouchEvent(eventBus, viewHolder));
        return viewHolder;
    }
}
