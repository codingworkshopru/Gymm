package ru.codingworkshop.gymm.service;

import com.google.common.eventbus.EventBus;

/**
 * Created by Радик on 26.09.2017 as part of the Gymm project.
 */

public interface RestEventBusHolder {
    EventBus getRestEventBus();
}
