package ru.codingworkshop.gymm.ui.program.events;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.common.eventbus.EventBus;

/**
 * Created by Радик on 28.04.2017.
 */

abstract class ViewEvent<B extends ViewDataBinding> {
    private View view;
    private B binding;
    private EventBus eventBus;

    public ViewEvent(@NonNull EventBus bus) {
        eventBus = bus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void setView(View v) {
        view = v;
    }

    public void setBinding(B b) {
        binding = b;
    }

    public View getView() {
        return view;
    }

    public B getBinding() {
        return binding;
    }
}
