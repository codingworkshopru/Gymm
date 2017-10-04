package ru.codingworkshop.gymm.ui.actual.exercise;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.databinding.ActivityActualTrainingStepperItemBinding;

/**
 * Created by Радик on 04.10.2017 as part of the Gymm project.
 */

public class ActualExercisesStepperView extends RecyclerView {

    public interface OnExerciseActivatedListener {
        void exerciseActivated(int activatedItem);
    }

    private OnExerciseActivatedListener listener;
    private View itemFloatingContainer;
    private int currentItemPosition = NO_POSITION;

    public ActualExercisesStepperView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setHasFixedSize(true);
    }

    @Override
    public ActualExercisesStepperAdapter getAdapter() {
        return (ActualExercisesStepperAdapter) super.getAdapter();
    }

    public void setAdapter(ActualExercisesStepperAdapter adapter) {
        adapter.addOnClickObserver(this::activateItem);
        super.setAdapter(adapter);
    }

    public void setOnExerciseActivatedListener(OnExerciseActivatedListener listener) {
        this.listener = listener;
    }

    public void setItemFloatingContainer(View itemFloatingContainer) {
        this.itemFloatingContainer = itemFloatingContainer;
    }

    public void setCurrentItemPosition(int position) {
        if (getCurrentItemPosition() == position) return;

        ViewHolder vh = findViewHolderForAdapterPosition(position);
        if (vh != null) {
            activateItem(vh.itemView);
        } else {
            addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
                @Override
                public void onChildViewAttachedToWindow(View view) {
                    if (getChildAdapterPosition(view) == position) {
                        activateItem(view);
                        removeOnChildAttachStateChangeListener(this);
                    }
                }
                public void onChildViewDetachedFromWindow(View view) {}
            });
            scrollToPosition(getCurrentItemPosition());
        }
    }

    public boolean goToNext() {
        final int newItemPosition = currentItemPosition + 1;
        final Adapter adapter = super.getAdapter();
        if (newItemPosition != 0 && newItemPosition < adapter.getItemCount()) {
            adapter.notifyItemChanged(currentItemPosition);
            setCurrentItemPosition(newItemPosition);
            return true;
        }

        return false;
    }

    public int getCurrentItemPosition() {
        return currentItemPosition;
    }

    private void activateItem(View itemView) {
        ActivityActualTrainingStepperItemBinding oldBinding = DataBindingUtil.findBinding(itemFloatingContainer);
        if (oldBinding != null) {
            oldBinding.setActive(false);
        }
        ActivityActualTrainingStepperItemBinding binding = DataBindingUtil.getBinding(itemView);
        binding.setActive(true);
        moveFloatingContainer(itemView);

        final int index = binding.getIndex();
        currentItemPosition = index;
        if (listener != null) {
            listener.exerciseActivated(index);
        }
    }

    private void moveFloatingContainer(View toItem) {
        ViewParent fromItem = itemFloatingContainer.getParent();
        if (fromItem != null) {
            FrameLayout layout = (FrameLayout) fromItem;
            layout.removeView(itemFloatingContainer);
        }

        FrameLayout layout = toItem.findViewById(R.id.stepperItemActualSetsContainer);
        layout.addView(itemFloatingContainer);
    }
}
