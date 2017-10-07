package ru.codingworkshop.gymm.ui.program.training;

import android.annotation.SuppressLint;
import android.content.Context;
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

    public ProgramExercisesAdapter(Context context, @Nullable List<ProgramExerciseNode> items) {
        super(context, R.layout.fragment_program_training_list_item, items);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected FragmentProgramTrainingListItemBinding createBinding(ViewGroup parent) {
        FragmentProgramTrainingListItemBinding binding = super.createBinding(parent);
        binding.programExerciseReorderImage.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (reorderDownListener != null) {
                    reorderDownListener.onClick(view);
                }
                return true;
            }
            return false;
        });
        return binding;
    }

    public void setReorderDownListener(View.OnClickListener reorderDownListener) {
        this.reorderDownListener = reorderDownListener;
    }

    @Override
    protected void bind(FragmentProgramTrainingListItemBinding binding, ProgramExerciseNode item) {
        binding.setExerciseName(item.getExercise().getName());
        binding.setSetsCount(item.getChildren().size());
    }
}
