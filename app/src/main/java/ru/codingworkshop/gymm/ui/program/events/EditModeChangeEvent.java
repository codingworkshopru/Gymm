package ru.codingworkshop.gymm.ui.program.events;

/**
 * Created by Радик on 30.04.2017.
 */

public final class EditModeChangeEvent {
    public boolean inEditMode;

    public EditModeChangeEvent(boolean inEditMode) {
        this.inEditMode = inEditMode;
    }

}
