package ru.codingworkshop.gymm.ui.program.event;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.common.eventbus.EventBus;

/**
 * Created by Радик on 28.04.2017.
 */

public class ClickViewEvent<B extends ViewDataBinding> extends ViewEvent<B> implements View.OnClickListener {
    public ClickViewEvent(@NonNull EventBus bus) {
        super(bus);
    }

    @Override
    public void onClick(View v) {
        setView(v);
        getEventBus().post(this);
    }

    // factory methods
    public static <B extends ViewDataBinding> ClickViewEvent<B> createClickEvent(@NonNull EventBus bus, B binding) {
        ClickViewEvent<B> resultEvent = new ClickViewEvent<>(bus);
        resultEvent.setBinding(binding);
        return resultEvent;
    }
}
