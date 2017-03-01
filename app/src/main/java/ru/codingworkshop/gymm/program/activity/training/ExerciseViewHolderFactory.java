package ru.codingworkshop.gymm.program.activity.training;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.databinding.ActivityProgramTrainingListItemBinding;
import ru.codingworkshop.gymm.program.ProgramAdapter;
import ru.codingworkshop.gymm.program.ProgramViewHolder;
import ru.codingworkshop.gymm.program.ViewHolderFactory;

/**
 * Created by Радик on 28.02.2017.
 */

public class ExerciseViewHolderFactory implements ViewHolderFactory<ExerciseViewHolder> {
    @Override
    public ExerciseViewHolder createViewHolder(ViewGroup root, ProgramAdapter adapter) {
        LayoutInflater layoutInflater = LayoutInflater.from(root.getContext());
        ActivityProgramTrainingListItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_program_training_list_item, root, false);
        binding.setAdapter(adapter);

        View itemView = binding.getRoot();
        ExerciseViewHolder vh = new ExerciseViewHolder(itemView, binding);
        vh.setReorderActionView(itemView.findViewById(R.id.program_training_list_item_reorder_action));
        return vh;
    }
}
