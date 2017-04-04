package ru.codingworkshop.gymm.program.activity.exercise.picker.exercises;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.model.Exercise;

/**
 * Created by Радик on 30.03.2017.
 */
class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ViewHolder> {
    private List<Exercise> exercises;
    private OnExerciseClickListener itemClickListener;
    private Context context;

    private static final String TAG = ExercisesAdapter.class.getSimpleName();
    private static final int DIVIDER_VIEW_TYPE = 2;

    interface OnExerciseClickListener {
        void onExerciseClick(View view);
        void onExerciseInfoClick(View view);
    }

    ExercisesAdapter(OnExerciseClickListener listener, Context context) {
        itemClickListener = listener;
        this.context = context;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
        notifyDataSetChanged();
    }

    public Exercise getItem(int position) {
        return exercises.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        @LayoutRes int layoutId = R.layout.activity_exercises_list_item;

        if (viewType == DIVIDER_VIEW_TYPE)
            layoutId = R.layout.list_subheader_item;

        final View itemView = inflater.inflate(layoutId, parent, false);

        if (viewType == 0) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onExerciseClick(v);
                }
            });

            itemView.findViewById(R.id.imageButton4).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onExerciseInfoClick(itemView);
                }
            });
        }

        return new ViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return exercises.get(position).isPhantom() ? DIVIDER_VIEW_TYPE : super.getItemViewType(position);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        @StringRes int stringRes = position == 0 ? R.string.exercises_picker_activity_primary : R.string.exercises_picker_activity_secondary;
        if (getItemViewType(position) == DIVIDER_VIEW_TYPE) {
            holder.setSubheader(context.getString(stringRes));
        } else {
            holder.setText(exercises.get(position).getName());
        }
    }

    @Override
    public int getItemCount() {
        return exercises == null ? 0 : exercises.size();
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
        }

        void setSubheader(String subheader) {
            ((TextView) itemView.findViewById(R.id.list_subheader_text)).setText(subheader);
        }

        void setText(String text) {
            ((TextView) itemView.findViewById(R.id.textView8)).setText(text);
        }
    }
}
