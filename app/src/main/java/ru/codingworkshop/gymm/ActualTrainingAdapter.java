package ru.codingworkshop.gymm;

import android.content.Context;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StyleRes;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ru.codingworkshop.gymm.data.model.ProgramTraining;

/**
 * Created by Радик on 18.03.2017.
 */

public class ActualTrainingAdapter extends RecyclerView.Adapter<ActualTrainingAdapter.ViewHolder> {

    public interface OnStepClickListener {
        void onStepClick(View view);
    }

    private OnStepClickListener onStepClickListener;
    private ProgramTraining programTraining;
    private int activePosition = 0;

    private static final int ITEM_TYPE_INACTIVE = 0;
    private static final int ITEM_TYPE_ACTIVE = 1;

    public ActualTrainingAdapter(OnStepClickListener listener) {
        onStepClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return programTraining != null ? programTraining.getExercises().size() : 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View stepContent = inflater.inflate(R.layout.activity_actual_training_stepper_item, parent, false);
        Context context = parent.getContext();

        if (viewType == ITEM_TYPE_ACTIVE) {
            final @DrawableRes int activeCircle = R.drawable.ic_circle_primary_24dp;
            final @StyleRes int activeTitle = R.style.TextAppearance_AppCompat_Body2;

            ImageView circle = (ImageView) stepContent.findViewById(R.id.imageView2);
            circle.setImageResource(activeCircle);

            TextView title = (TextView) stepContent.findViewById(R.id.textView3);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                title.setTextAppearance(context, activeTitle);
            else
                title.setTextAppearance(activeTitle);

            @IdRes int aboveViewId = R.id.linearLayout;
            @IdRes int fragmentContainerId = ActualTrainingActivity.FRAGMENT_CONTAINER_ID;
            FrameLayout fragmentContainer = new FrameLayout(context);
            fragmentContainer.setId(fragmentContainerId);

            ConstraintLayout newActiveConstraint = (ConstraintLayout) stepContent;

            newActiveConstraint.addView(fragmentContainer);
            ConstraintSet s = new ConstraintSet();
            s.constrainHeight(fragmentContainerId, ConstraintSet.WRAP_CONTENT);
            s.constrainWidth(fragmentContainerId, 0);
            s.connect(fragmentContainerId, ConstraintSet.LEFT, aboveViewId, ConstraintSet.LEFT);
            s.connect(fragmentContainerId, ConstraintSet.RIGHT, aboveViewId, ConstraintSet.RIGHT);
            s.connect(fragmentContainerId, ConstraintSet.TOP, aboveViewId, ConstraintSet.BOTTOM);
            s.applyTo(newActiveConstraint);

            ((AppCompatActivity) onStepClickListener).getSupportFragmentManager().beginTransaction().add(ActualTrainingActivity.FRAGMENT_CONTAINER_ID, new BlankFragment()).commit();
        } else {
            stepContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onStepClickListener.onStepClick(v);
                }
            });
        }

        return new ViewHolder(stepContent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setIndex(position + 1);
        holder.setTitle(programTraining.getExercises().get(position).getExercise().getName());

        holder.setTopLineVisibility(position > 0);
        holder.setBottomLineVisibility(position < getItemCount() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        return position == activePosition ? ITEM_TYPE_ACTIVE : ITEM_TYPE_INACTIVE;
    }

    public void setModel(ProgramTraining model) {
        programTraining = model;
        setActivePosition(0);
    }

    public ProgramTraining getModel() {
        return programTraining;
    }

    public int getActivePosition() {
        return activePosition;
    }

    public void setActivePosition(int position) {
        activePosition = position;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView indexNumber;
        private TextView title;
        private TextView summary;
        private ImageView topVerticalLine;
        private ImageView bottomVerticalLine;
        private ImageView stepperCircle;

        public ViewHolder(View view) {
            super(view);
            indexNumber = (TextView) view.findViewById(R.id.textView4);
            topVerticalLine = (ImageView) view.findViewById(R.id.imageView4);
            bottomVerticalLine = (ImageView) view.findViewById(R.id.imageView3);
            stepperCircle = (ImageView) view.findViewById(R.id.imageView2);
            title = (TextView) view.findViewById(R.id.textView3);
            summary = (TextView) view.findViewById(R.id.textView5);
        }

        public void setTopLineVisibility(boolean visible) {
            topVerticalLine.setVisibility(visible ? View.VISIBLE : View.GONE);
        }

        public void setBottomLineVisibility(boolean visible) {
            bottomVerticalLine.setVisibility(visible ? View.VISIBLE : View.GONE);
        }

        public void setTitle(String text) {
            title.setText(text);
        }

        public void setIndex(int index) {
            indexNumber.setText(String.valueOf(index));
        }

        public void setSummaryText(String text) {
            summary.setText(text);
            summary.setVisibility(View.VISIBLE);
        }
    }
}
