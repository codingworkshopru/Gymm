package ru.codingworkshop.gymm.ui.program.events;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.common.eventbus.EventBus;

/**
 * Created by Радик on 28.04.2017.
 */

public class LongClickViewEvent<B extends ViewDataBinding> extends ViewEvent<B> implements View.OnLongClickListener {
    public LongClickViewEvent(@NonNull EventBus bus) {
        super(bus);
    }

    @Override
    public boolean onLongClick(View v) {
        setView(v);
        getEventBus().post(this);
        return true;
    }

    // factory methods
    public static <B extends ViewDataBinding> LongClickViewEvent<B> createLongClickEvent(@NonNull EventBus bus, B binding) {
        LongClickViewEvent<B> resultEvent = new LongClickViewEvent<>(bus);
        resultEvent.setBinding(binding);
        return resultEvent;
    }
}
