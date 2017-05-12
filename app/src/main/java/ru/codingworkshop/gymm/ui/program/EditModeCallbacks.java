package ru.codingworkshop.gymm.ui.program;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import ru.codingworkshop.gymm.ui.program.event.EditModeChangeEvent;
import ru.codingworkshop.gymm.ui.program.event.LongClickViewEvent;

/**
 * Created by Радик on 28.04.2017.
 */

public class EditModeCallbacks implements ActionMode.Callback {

    private EventBus eventBus;
    private Activity activity;
    private RecyclerView recyclerView;
    private @StringRes int deleteMessage;

    private ListItemCallbacks listItemCallbacks;

    public EditModeCallbacks(@NonNull EventBus bus, @NonNull Activity parentActivity, @NonNull RecyclerView rv, @StringRes int deleteMessageRes) {
        eventBus = bus;
        activity = parentActivity;
        recyclerView = rv;
        deleteMessage = deleteMessageRes;

        eventBus.register(this);
    }

    @Subscribe
    public void onItemLongClick(LongClickViewEvent event) {
        activity.startActionMode(this);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        eventBus.post(new EditModeChangeEvent(true));
        if (listItemCallbacks == null) {
            listItemCallbacks = new ListItemCallbacks(eventBus, (Adapter) recyclerView.getAdapter(), deleteMessage);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(listItemCallbacks);
            listItemCallbacks.setItemTouchHelper(itemTouchHelper);
        }
        listItemCallbacks.getItemTouchHelper().attachToRecyclerView(recyclerView);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        eventBus.post(new EditModeChangeEvent(false));
        listItemCallbacks.getItemTouchHelper().attachToRecyclerView(null);
    }
}
