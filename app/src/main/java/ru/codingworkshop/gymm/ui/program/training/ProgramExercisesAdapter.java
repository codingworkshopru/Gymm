package ru.codingworkshop.gymm.ui.program.training;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.databinding.FragmentProgramTrainingListItemBinding;
import ru.codingworkshop.gymm.ui.common.ClickableBindingListAdapter;

/**
 * Created by Радик on 07.10.2017 as part of the Gymm project.
 */

public class ProgramExercisesAdapter extends ClickableBindingListAdapter<ProgramExerciseNode, FragmentProgramTrainingListItemBinding> {
    private View.OnClickListener reorderDownListener;
    private View.OnLongClickListener longClickListener;
    private ObservableBoolean inActionMode;

    public ProgramExercisesAdapter(Context context, @Nullable List<ProgramExerciseNode> items, ObservableBoolean inActionMode) {
        super(context, R.layout.fragment_program_training_list_item, items);
        this.inActionMode = inActionMode;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected FragmentProgramTrainingListItemBinding createBinding(ViewGroup parent) {
        FragmentProgramTrainingListItemBinding binding = super.createBinding(parent);

        if (longClickListener != null) {
            binding.getRoot().setOnLongClickListener(longClickListener);
        }

        if (reorderDownListener != null) {
            binding.programExerciseReorderImage.setOnTouchListener((view, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    reorderDownListener.onClick(view);
                    return true;
                }
                return false;
            });
        }
        return binding;
    }

    public void setReorderDownListener(View.OnClickListener reorderDownListener) {
        this.reorderDownListener = reorderDownListener;
    }

    public void setLongClickListener(View.OnLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    @Override
    protected void bind(FragmentProgramTrainingListItemBinding binding, ProgramExerciseNode item) {
        binding.setExerciseName(item.getExercise().getName());
        binding.setSetsCount(item.getChildren().size());
        binding.setInActionMode(inActionMode);
    }
}
