package ru.codingworkshop.gymm.ui.program.events;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.google.common.eventbus.EventBus;

/**
 * Created by Радик on 28.04.2017.
 */

public class TouchViewEvent<B extends ViewDataBinding> extends ViewEvent<B> implements View.OnTouchListener {
    private MotionEvent motionEvent;
    private RecyclerView.ViewHolder viewHolder;

    public TouchViewEvent(@NonNull EventBus bus) {
        super(bus);
    }

    public void setMotionEvent(MotionEvent event) {
        motionEvent = event;
    }

    public MotionEvent getMotionEvent() {
        return motionEvent;
    }

    public void setViewHolder(@NonNull RecyclerView.ViewHolder vh) {
        viewHolder = vh;
    }

    public RecyclerView.ViewHolder getViewHolder() {
        return viewHolder;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        setView(v);
        setMotionEvent(event);
        getEventBus().post(this);
        return true;
    }

    // factory methods
    public static <B extends ViewDataBinding> TouchViewEvent<B> createTouchEvent(@NonNull EventBus bus, @NonNull RecyclerView.ViewHolder vh) {
        TouchViewEvent<B> resultEvent = new TouchViewEvent<>(bus);
        resultEvent.setViewHolder(vh);
        return resultEvent;
    }
}
