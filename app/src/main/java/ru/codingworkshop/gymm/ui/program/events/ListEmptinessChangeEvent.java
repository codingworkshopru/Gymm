package ru.codingworkshop.gymm.ui.program.events;

/**
 * Created by Радик on 30.04.2017.
 */

public final class ListEmptinessChangeEvent {
    public boolean listEmpty;

    public ListEmptinessChangeEvent(boolean listEmpty) {
        this.listEmpty = listEmpty;
    }
}
