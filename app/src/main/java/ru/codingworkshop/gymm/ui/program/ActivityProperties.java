package ru.codingworkshop.gymm.ui.program;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import ru.codingworkshop.gymm.BR;
import ru.codingworkshop.gymm.ui.program.events.EditModeChangeEvent;
import ru.codingworkshop.gymm.ui.program.events.ListEmptinessChangeEvent;

/**
 * Created by Радик on 30.04.2017.
 */

public final class ActivityProperties extends BaseObservable {
    private boolean inEditMode;
    private boolean listEmpty;

    public ActivityProperties(EventBus bus) {
        bus.register(this);
    }

    @Bindable
    public boolean isInEditMode() {
        return inEditMode;
    }

    @Bindable
    public boolean isListEmpty() {
        return listEmpty;
    }

    @Subscribe
    public void editModeChange(EditModeChangeEvent event) {
        inEditMode = event.inEditMode;
        notifyPropertyChanged(BR.inEditMode);
    }


    @Subscribe
    public void listEmptinessChange(ListEmptinessChangeEvent event) {
        listEmpty = event.listEmpty;
        notifyPropertyChanged(BR.listEmpty);
    }
}
