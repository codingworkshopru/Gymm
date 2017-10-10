package ru.codingworkshop.gymm.ui.program.common;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Радик on 10.10.2017 as part of the Gymm project.
 */

public class ProgramRecyclerView extends RecyclerView {
    private ItemTouchHelper itemTouchHelper;

    public ProgramRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setHasFixedSize(true);
        addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    public void setItemTouchHelperCallback(ItemTouchHelper.Callback itemTouchHelperCallback) {
        itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
    }

    public void attachItemTouchHelper() {
        itemTouchHelper.attachToRecyclerView(this);
    }

    public void detachItemTouchHelper() {
        itemTouchHelper.attachToRecyclerView(null);
    }

    public void startDragFromChildView(View v) {
        ViewHolder vh = findContainingViewHolder(v);
        if (vh != null) {
            itemTouchHelper.startDrag(vh);
        }
    }
}
