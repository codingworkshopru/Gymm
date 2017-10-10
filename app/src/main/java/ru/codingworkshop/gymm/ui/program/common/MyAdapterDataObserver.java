package ru.codingworkshop.gymm.ui.program.common;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import ru.codingworkshop.gymm.data.tree.ChildRestoreAdapter;
import ru.codingworkshop.gymm.data.tree.node.BaseNode;
import ru.codingworkshop.gymm.ui.util.UiUtil;

/**
 * Created by Радик on 08.10.2017 as part of the Gymm project.
 */
public class MyAdapterDataObserver extends RecyclerView.AdapterDataObserver {

    private RecyclerView recyclerView;
    private int backgroundImage;
    private int deleteMessage;
    private BaseNode node;

    public MyAdapterDataObserver(RecyclerView recyclerView, @IdRes int backgroundImage, @StringRes int deleteMessage, BaseNode node) {
        this.recyclerView = recyclerView;
        this.backgroundImage = backgroundImage;
        this.deleteMessage = deleteMessage;
        this.node = node;

        setBackgroundVisibility();
    }

    private void setBackgroundVisibility() {
        final View view = recyclerView.getRootView().findViewById(backgroundImage);
        int visibility = view.getVisibility();
        int newVisibility = node.getChildren().size() == 0 ? View.VISIBLE : View.GONE;
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
                    new ChildRestoreAdapter(node).restoreLastRemoved();
                    recyclerView.getAdapter().notifyItemInserted(positionStart);
                }
        );
        setBackgroundVisibility();
    }
}
