package ru.codingworkshop.gymm.ui.program.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.google.common.base.Preconditions;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.tree.node.BaseNode;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;

/**
 * Created by Радик on 08.10.2017 as part of the Gymm project.
 */
public class MySimpleCallback extends ItemTouchHelper.SimpleCallback {
    private BaseNode node;
    private Bitmap imageDrawable;
    private int imageWidth;
    private int imageHeight;

    public MySimpleCallback(BaseNode node) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.node = node;
    }

    private void prepareImage(Context context) {
        if (imageDrawable != null && !imageDrawable.isRecycled()) {
            return;
        }

        Drawable d = Preconditions.checkNotNull(
                ContextCompat.getDrawable(context, R.drawable.ic_delete_black_24dp),
                "Cannot create drawable from resource"
        );

        imageWidth = d.getIntrinsicWidth();
        imageHeight = d.getIntrinsicHeight();
        d.setBounds(0, 0, imageWidth, imageHeight);
        imageDrawable = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
        Canvas imageCanvas = new Canvas(imageDrawable);
        d.draw(imageCanvas);
    }

    private void recycleImage() {
        if (imageDrawable != null && !imageDrawable.isRecycled()) {
            imageDrawable.recycle();
            imageDrawable = null;
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        int from = source.getAdapterPosition();
        int to = target.getAdapterPosition();
        node.moveChild(from, to);
        recyclerView.getAdapter().notifyItemMoved(from, to);
        return from != to;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        final int index = viewHolder.getAdapterPosition();
        node.removeChild(index);
        ((RecyclerView) viewHolder.itemView.getParent()).getAdapter().notifyItemRemoved(index);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (isCurrentlyActive && actionState == ACTION_STATE_SWIPE) {
            prepareImage(recyclerView.getContext());

            final View view = viewHolder.itemView;
            final float top = view.getTop() + view.getHeight() / 2f - imageHeight / 2f;
            if (dX > 0f) {
                c.drawBitmap(imageDrawable, imageWidth, top, null);
            } else {
                c.drawBitmap(imageDrawable, view.getWidth() - 2 * imageWidth, top, null);
            }
        }

        if (!isCurrentlyActive) {
            recycleImage();
        }
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }
}
