package ru.codingworkshop.gymm.ui.actual.exercise;

import android.support.annotation.Nullable;

import java.util.List;

import ru.codingworkshop.gymm.data.tree.node.ActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.databinding.ActivityActualTrainingStepperItemBinding;
import ru.codingworkshop.gymm.ui.common.ClickableBindingListAdapter;
import ru.codingworkshop.gymm.ui.common.ListItemListeners;
import timber.log.Timber;

/**
 * Created by Радик on 16.09.2017 as part of the Gymm project.
 */
public class ActualExercisesStepperAdapter extends
        ClickableBindingListAdapter<ActualExerciseNode, ActivityActualTrainingStepperItemBinding> {

    public ActualExercisesStepperAdapter(@Nullable List<ActualExerciseNode> items, ListItemListeners listeners) {
        super(items, listeners);
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
