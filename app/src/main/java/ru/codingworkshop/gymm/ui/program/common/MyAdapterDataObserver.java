package ru.codingworkshop.gymm.ui.program.common;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import ru.codingworkshop.gymm.ui.util.UiUtil;

/**
 * Created by Радик on 08.10.2017 as part of the Gymm project.
 */
public abstract class MyAdapterDataObserver extends RecyclerView.AdapterDataObserver {

    private RecyclerView recyclerView;
    private int backgroundImage;
    private int deleteMessage;

    public MyAdapterDataObserver(RecyclerView recyclerView, @IdRes int backgroundImage, @StringRes int deleteMessage) {
        this.recyclerView = recyclerView;
        this.backgroundImage = backgroundImage;
        this.deleteMessage = deleteMessage;
    }

    private void setBackgroundVisibility() {
        final View view = recyclerView.getRootView().findViewById(backgroundImage);
        int visibility = view.getVisibility();
        int newVisibility = recyclerView.getAdapter().getItemCount() == 0 ? View.VISIBLE : View.GONE;
        if (visibility != newVisibility) {
            view.setVisibility(newVisibility);
        }
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        setBackgroundVisibility();
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        UiUtil.showSnackbar(
                recyclerView,
                deleteMessage,
                android.R.string.cancel,
                v -> {
                    restoreLastRemoved();
                    recyclerView.getAdapter().notifyItemInserted(positionStart);
                }
        );
        setBackgroundVisibility();
    }

    public abstract void restoreLastRemoved();
}
