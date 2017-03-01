package ru.codingworkshop.gymm.program.activity.training;

import android.view.View;

import ru.codingworkshop.gymm.data.model.ProgramExercise;
import ru.codingworkshop.gymm.databinding.ActivityProgramTrainingListItemBinding;
import ru.codingworkshop.gymm.program.ProgramViewHolder;

/**
 * Created by Радик on 28.02.2017.
 */

final class ExerciseViewHolder extends ProgramViewHolder<ProgramExercise> {
    private ActivityProgramTrainingListItemBinding mBinding;

    ExerciseViewHolder(View itemView, ActivityProgramTrainingListItemBinding binding) {
        super(itemView);
        mBinding = binding;
    }

    @Override
    public void setModel(ProgramExercise model) {
        mBinding.setModel(model);
    }
}
