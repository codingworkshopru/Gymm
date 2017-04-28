package ru.codingworkshop.gymm.ui.program;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import ru.codingworkshop.gymm.ui.program.events.TouchViewEvent;

/**
 * Created by Радик on 28.04.2017.
 */

public class ListItemActions extends ItemTouchHelper.SimpleCallback implements View.OnClickListener {
    private ItemTouchHelper itemTouchHelper;
    private Adapter adapter;
    private
    @StringRes
    int deletedMessageId;

    public ListItemActions(@NonNull EventBus bus, @NonNull Adapter a, @StringRes int deletedMessage) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT);
        adapter = a;
        deletedMessageId = deletedMessage;

        bus.register(this);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();

        adapter.moveModel(fromPosition, toPosition);

        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int deletedItemPosition = viewHolder.getAdapterPosition();

        adapter.removeModel(deletedItemPosition);

        UiUtil.showSnackbar(viewHolder.itemView, deletedMessageId, android.R.string.cancel, this);
    }

    @Override
    public void onClick(View v) {
        adapter.restoreLastRemoved();
    }

    @Subscribe
    public void onReorderActionTouch(TouchViewEvent event) {
        MotionEvent me = event.getMotionEvent();
        if (MotionEventCompat.getActionMasked(me) == MotionEvent.ACTION_DOWN) {
            itemTouchHelper.startDrag(event.getViewHolder());
        }
    }

    public void setItemTouchHelper(@NonNull ItemTouchHelper touchHelper) {
        itemTouchHelper = touchHelper;
    }
}
