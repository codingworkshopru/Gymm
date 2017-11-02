package ru.codingworkshop.gymm.ui.program.exercise;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;

import java.util.List;

import ru.codingworkshop.gymm.R;
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

    private String get(Context context, @PluralsRes int plural, int arg) {
        return context.getResources().getQuantityString(plural, arg, arg);
    }

    @Override
    protected void bind(FragmentProgramExerciseListItemBinding binding, ProgramSet item) {
        binding.setRepsCount(item.getReps());
        String timeRest = null;
        if (item.getSecondsForRest() != null) {
            Context c = binding.getRoot().getContext();
            int secondsForRest = item.getSecondsForRest();
            timeRest = get(c, R.plurals.minutes, secondsForRest / 60) + ' ' + get(c,
                    R.plurals.seconds, secondsForRest % 60);
        }
        binding.setRestTime(timeRest);
        binding.setInActionMode(inActionMode);
    }
}
