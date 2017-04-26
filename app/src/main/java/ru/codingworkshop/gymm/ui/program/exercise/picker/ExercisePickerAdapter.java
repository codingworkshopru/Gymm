package ru.codingworkshop.gymm.ui.program.exercise.picker;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.common.collect.Lists;

import java.util.List;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.model.Exercise;
import ru.codingworkshop.gymm.data.model.MuscleGroup;
import ru.codingworkshop.gymm.databinding.ActivityExercisesListItemBinding;
import ru.codingworkshop.gymm.databinding.ListSubheaderItemBinding;
import ru.codingworkshop.gymm.ui.program.BindingHolder;

/**
 * Created by Радик on 23.04.2017.
 */

public final class ExercisePickerAdapter extends RecyclerView.Adapter<BindingHolder<ViewDataBinding>> {

    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise);
        void onExerciseInfoClick(Exercise exercise);
    }

    private static final @LayoutRes int EXERCISE_ITEM = R.layout.activity_exercises_list_item;
    private static final @LayoutRes int SUBHEADER_ITEM = R.layout.list_subheader_item;

    private List<Exercise> exercises;
    private OnExerciseClickListener handler;

    ExercisePickerAdapter(MuscleGroup muscleGroup, OnExerciseClickListener handler) {
        List<Exercise> forPrimary = muscleGroup.getExercisesForPrimary();
        List<Exercise> forSecondary = muscleGroup.getExercisesForSecondary();
        exercises = Lists.newArrayList(forPrimary);
        if (!forSecondary.isEmpty()) {
            exercises.add(null); // divider
            exercises.addAll(forSecondary);
        }

        this.handler = handler;
    }

    @Override
    public BindingHolder<ViewDataBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, viewType, parent, false);
        return new BindingHolder(binding);
    }

    @Override
    public void onBindViewHolder(BindingHolder<ViewDataBinding> holder, int position) {
        ViewDataBinding binding = holder.binding;
        Exercise exercise = exercises.get(position);
        if (exercise == null) {
            String title = binding.getRoot().getContext().getString(R.string.exercises_picker_activity_secondary);
            ((ListSubheaderItemBinding) binding).setSubheader(title);
        } else {
            ActivityExercisesListItemBinding exercieItemBinding = (ActivityExercisesListItemBinding) binding;
            exercieItemBinding.setExercise(exercise);
            exercieItemBinding.setHandler(handler);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (exercises.get(position) == null)
            return SUBHEADER_ITEM;

        return EXERCISE_ITEM;
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }
}
