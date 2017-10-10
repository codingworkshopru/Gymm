package ru.codingworkshop.gymm.ui.common;

import android.annotation.SuppressLint;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.View;

import static android.view.MotionEvent.ACTION_DOWN;

/**
 * Created by Радик on 10.10.2017 as part of the Gymm project.
 */

public class ListItemListeners {
    private @LayoutRes int layout;
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;
    private View.OnTouchListener onReorderButtonDownListener;
    private @IdRes int reorderButtonId;

    public ListItemListeners(int layout) {
        this.layout = layout;
    }

    public void setListenersToView(View view) {
        if (onClickListener != null) {
            view.setOnClickListener(onClickListener);
        }

        if (onLongClickListener != null) {
            view.setOnLongClickListener(onLongClickListener);
        }

        if (onReorderButtonDownListener != null) {
            view.findViewById(reorderButtonId).setOnTouchListener(onReorderButtonDownListener);
        }
    }

    public int getLayout() {
        return layout;
    }

    public ListItemListeners setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public ListItemListeners setOnLongClickListener(View.OnClickListener onLongClickListener) {
        this.onLongClickListener = v -> {
            onLongClickListener.onClick(v);
            return true;
        };
        return this;
    }

    public View.OnLongClickListener getOnLongClickListener() {
        return onLongClickListener;
    }

    @SuppressLint("ClickableViewAccessibility")
    public ListItemListeners setOnReorderButtonDownListener(View.OnClickListener onReorderButtonDownListener, @IdRes int reorderButtonId) {
        this.onReorderButtonDownListener = (v, event) -> {
            if (event.getAction() == ACTION_DOWN) {
                onReorderButtonDownListener.onClick(v);
                return true;
            }
            return false;
        };
        this.reorderButtonId = reorderButtonId;
        return this;
    }

    public View.OnTouchListener getOnReorderButtonDownListener() {
        return onReorderButtonDownListener;
    }

    public @IdRes int getReorderButtonId() {
        return reorderButtonId;
    }
}
