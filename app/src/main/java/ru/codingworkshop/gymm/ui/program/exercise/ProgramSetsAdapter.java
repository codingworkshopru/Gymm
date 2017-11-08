package ru.codingworkshop.gymm.ui.program.exercise;

import android.databinding.ObservableBoolean;
import android.support.annotation.Nullable;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.databinding.FragmentProgramExerciseListItemBinding;
import ru.codingworkshop.gymm.ui.common.ClickableBindingListAdapter;
import ru.codingworkshop.gymm.ui.common.ListItemListeners;

/**
 * Created by Радик on 17.10.2017 as part of the Gymm project.
 */
class ProgramSetsAdapter extends ClickableBindingListAdapter<ProgramSet, FragmentProgramExerciseListItemBinding> {
    private ObservableBoolean inActionMode;

    ProgramSetsAdapter(ListItemListeners listItemListeners, @Nullable List<ProgramSet> items, ObservableBoolean inActionMode) {
        super(items, listItemListeners);
        this.inActionMode = inActionMode;
    }

    @Override
    protected void bind(FragmentProgramExerciseListItemBinding binding, ProgramSet item) {
        binding.setWrappedSet(new ProgramSetWrapper(binding.getRoot().getContext(), item));
        binding.setInActionMode(inActionMode);
    }
}
