package ru.codingworkshop.gymm.ui.program.event;

import com.google.common.eventbus.EventBus;

/**
 * Created by Радик on 30.04.2017.
 */

public final class ListEmptinessChangeEvent {
    public boolean listEmpty;

    public ListEmptinessChangeEvent(boolean listEmpty) {
        this.listEmpty = listEmpty;
    }

    public static void post(EventBus bus, boolean listEmpty) {
        bus.post(new ListEmptinessChangeEvent(listEmpty));
    }
}
