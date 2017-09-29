package ru.codingworkshop.gymm.service;

import android.support.annotation.NonNull;

import com.google.common.eventbus.EventBus;

/**
 * Created by Радик on 26.09.2017 as part of the Gymm project.
 */

public interface RestEventBusHolder {
    @NonNull EventBus getRestEventBus();
}
