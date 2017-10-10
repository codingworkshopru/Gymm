package ru.codingworkshop.gymm.ui.program.common;

import android.databinding.ObservableBoolean;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Радик on 10.10.2017 as part of the Gymm project.
 */

public class ActionModeCallback implements ActionMode.Callback {
    private ProgramRecyclerView recyclerView;
    private ObservableBoolean inActionMode;

    public ActionModeCallback(ProgramRecyclerView recyclerView, ObservableBoolean inActionMode) {
        this.recyclerView = recyclerView;
        this.inActionMode = inActionMode;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        recyclerView.attachItemTouchHelper();
        inActionMode.set(true);
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
        recyclerView.detachItemTouchHelper();
        inActionMode.set(false);
    }
}
