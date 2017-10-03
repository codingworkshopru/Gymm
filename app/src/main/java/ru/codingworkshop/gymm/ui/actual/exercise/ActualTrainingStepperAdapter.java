package ru.codingworkshop.gymm.ui.actual.exercise;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.tree.node.ActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.databinding.ActivityActualTrainingStepperItemBinding;
import ru.codingworkshop.gymm.ui.common.BindingListAdapter;
import timber.log.Timber;

/**
 * Created by Радик on 16.09.2017 as part of the Gymm project.
 */
public class ActualTrainingStepperAdapter extends
        BindingListAdapter<ActualExerciseNode, ActivityActualTrainingStepperItemBinding> {

    private Context context;
    private OnExerciseActivateListener onItemClickListener;
    private ActivityActualTrainingStepperItemBinding activeBinding;

    public interface OnExerciseActivateListener {
        void onExerciseActivate(ActivityActualTrainingStepperItemBinding oldItemBinding,
                                       ActivityActualTrainingStepperItemBinding newItemBinding);
    }

    public ActualTrainingStepperAdapter(@Nullable List<ActualExerciseNode> items, Context context,
                                        OnExerciseActivateListener onItemClickListener) {

        super(items);

        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected ActivityActualTrainingStepperItemBinding createBinding(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ActivityActualTrainingStepperItemBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.activity_actual_training_stepper_item, parent, false);

        binding.getRoot().setOnClickListener(v -> {
            if (activeBinding == binding) return;

            ActivityActualTrainingStepperItemBinding oldItemBinding = activeBinding;
            setActiveBinding(binding);
            onItemClickListener.onExerciseActivate(oldItemBinding, binding);
        });

        return binding;
    }

    public ActivityActualTrainingStepperItemBinding getActiveBinding() {
        return activeBinding;
    }

    private void setActiveBinding(ActivityActualTrainingStepperItemBinding binding) {
        if (activeBinding != null) {
            activeBinding.setActive(false);
        }
        binding.setActive(true);
        activeBinding = binding;
    }

    @Override
    protected void bind(ActivityActualTrainingStepperItemBinding binding, ActualExerciseNode item) {
        final ProgramExerciseNode programExerciseNode = item.getProgramExerciseNode();
        Timber.d("binding exercise " + programExerciseNode.getExercise().getName());
        final int sortOrder = programExerciseNode.getSortOrder();
        binding.setFirst(sortOrder == 0);
        binding.setLast(sortOrder == getItemCount() - 1);
        binding.setIndex(sortOrder);
        binding.setTitle(programExerciseNode.getExercise().getName());
        binding.setDone(item.getChildren().size() == item.getProgramExerciseNode().getChildren().size());
    }
}
