package ru.codingworkshop.gymm.ui.actual;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.tree.node.ActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.databinding.ActivityActualTrainingStepperItemBinding;
import ru.codingworkshop.gymm.ui.common.BindingListAdapter;

/**
 * Created by Радик on 16.09.2017 as part of the Gymm project.
 */
class ActualTrainingStepperAdapter extends BindingListAdapter<ActualExerciseNode, ActivityActualTrainingStepperItemBinding> {
    private Context context;
    private View.OnClickListener listener;

    public ActualTrainingStepperAdapter(@Nullable List<ActualExerciseNode> items, Context context, View.OnClickListener listener) {
        super(items);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected ActivityActualTrainingStepperItemBinding createBinding(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ActivityActualTrainingStepperItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.activity_actual_training_stepper_item, parent, false);

        binding.getRoot().setOnClickListener(listener);

        return binding;
    }

    @Override
    protected void bind(ActivityActualTrainingStepperItemBinding binding, ActualExerciseNode item) {
        final ProgramExerciseNode programExerciseNode = item.getProgramExerciseNode();
        final int sortOrder = programExerciseNode.getSortOrder();
        binding.setFirst(sortOrder == 0);
        binding.setLast(sortOrder == getItemCount() - 1);
        binding.setIndex(sortOrder);
        binding.setTitle(programExerciseNode.getExercise().getName());
    }
}
