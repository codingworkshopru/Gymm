package ru.codingworkshop.gymm.program.activity.exercise;

import android.view.View;

import ru.codingworkshop.gymm.data.model.ProgramSet;
import ru.codingworkshop.gymm.databinding.ActivityProgramExerciseListItemBinding;
import ru.codingworkshop.gymm.program.ProgramViewHolder;

/**
 * Created by Радик on 25.02.2017.
 */

final class SetViewHolder extends ProgramViewHolder<ProgramSet> {
    private ActivityProgramExerciseListItemBinding mBinding;

    SetViewHolder(View itemView, ActivityProgramExerciseListItemBinding binding) {
        super(itemView);
        mBinding = binding;
    }

    @Override
    public void setModel(ProgramSet model) {
        mBinding.setModel(model);
    }
}
