package ru.codingworkshop.gymm.ui.program.training;

import android.databinding.ObservableBoolean;
import android.support.annotation.Nullable;

import java.util.List;

import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.databinding.FragmentProgramTrainingListItemBinding;
import ru.codingworkshop.gymm.ui.common.ClickableBindingListAdapter;
import ru.codingworkshop.gymm.ui.common.ListItemListeners;

/**
 * Created by Радик on 07.10.2017 as part of the Gymm project.
 */

public class ProgramExercisesAdapter extends ClickableBindingListAdapter<ProgramExerciseNode, FragmentProgramTrainingListItemBinding> {
    private ObservableBoolean inActionMode;

    public ProgramExercisesAdapter(ListItemListeners listItemListeners, @Nullable List<ProgramExerciseNode> items, ObservableBoolean inActionMode) {
        super(items, listItemListeners);
        this.inActionMode = inActionMode;
    }

    @Override
    protected void bind(FragmentProgramTrainingListItemBinding binding, ProgramExerciseNode item) {
        binding.setExerciseName(item.getExercise().getName());
        binding.setSetsCount(item.getChildren().size());
        binding.setInActionMode(inActionMode);
    }
}
