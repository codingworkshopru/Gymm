package ru.codingworkshop.gymm.ui.actual.exercise;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
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
public class ActualExercisesStepperAdapter extends
        BindingListAdapter<ActualExerciseNode, ActivityActualTrainingStepperItemBinding> {

    private Context context;
    private List<Observer<View>> itemClickObservers;

    public ActualExercisesStepperAdapter(@Nullable List<ActualExerciseNode> items, Context context) {
        super(items);
        this.context = context;
    }

    @Override
    protected ActivityActualTrainingStepperItemBinding createBinding(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ActivityActualTrainingStepperItemBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.activity_actual_training_stepper_item, parent, false);

        binding.getRoot().setOnClickListener(this::notifyOnClickObservers);

        return binding;
    }

    void addOnClickObserver(Observer<View> observer) {
        if (itemClickObservers == null) {
            itemClickObservers = new LinkedList<>();
        }
        itemClickObservers.add(observer);
    }

    void removeOnClickObserver(Observer<View> observer) {
        if (hasItemClickObservers()) {
            itemClickObservers.remove(observer);
        }
    }

    private boolean hasItemClickObservers() {
        return itemClickObservers != null && !itemClickObservers.isEmpty();
    }

    private void notifyOnClickObservers(View binding) {
        if (hasItemClickObservers()) {
            for (Observer<View> o : itemClickObservers) {
                o.onChanged(binding);
            }
        }
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
