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

import ru.codingworkshop.gymm.ui.program.events.EditModeChangeEvent;
import ru.codingworkshop.gymm.ui.program.events.LongClickViewEvent;

/**
 * Created by Радик on 28.04.2017.
 */

public class EditModeActions implements ActionMode.Callback {

    private EventBus eventBus;
    private Activity activity;
    private RecyclerView recyclerView;
    private @StringRes int deleteMessage;

    private ListItemActions listItemActions;

    public EditModeActions(@NonNull EventBus bus, @NonNull Activity parentActivity, @NonNull RecyclerView rv, @StringRes int deleteMessageRes) {
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
        if (listItemActions == null) {
            listItemActions = new ListItemActions(eventBus, (Adapter) recyclerView.getAdapter(), deleteMessage);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(listItemActions);
            listItemActions.setItemTouchHelper(itemTouchHelper);
        }
        listItemActions.getItemTouchHelper().attachToRecyclerView(recyclerView);
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
        listItemActions.getItemTouchHelper().attachToRecyclerView(null);
    }
}
