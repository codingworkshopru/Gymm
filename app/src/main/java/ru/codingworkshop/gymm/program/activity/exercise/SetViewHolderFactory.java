package ru.codingworkshop.gymm.program.activity.exercise;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.codingworkshop.gymm.R;import ru.codingworkshop.gymm.databinding.ActivityProgramExerciseListItemBinding;

import ru.codingworkshop.gymm.program.ProgramAdapter;
import ru.codingworkshop.gymm.program.ViewHolderFactory;

/**
 * Created by Радик on 28.02.2017.
 */

public class SetViewHolderFactory implements ViewHolderFactory<SetViewHolder> {

    @Override
    public SetViewHolder createViewHolder(ViewGroup root, ProgramAdapter adapter) {
        LayoutInflater layoutInflater = LayoutInflater.from(root.getContext());
        ActivityProgramExerciseListItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_program_exercise_list_item, root, false);
        binding.setAdapter(adapter);

        View itemView = binding.getRoot();
        SetViewHolder vh = new SetViewHolder(itemView, binding);
        vh.setReorderActionView(itemView.findViewById(R.id.program_exercise_list_item_reorder_action));
        return vh;
    }
}
