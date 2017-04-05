package ru.codingworkshop.gymm;

import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.codingworkshop.gymm.data.model.ProgramTraining;

/**
 * Created by Радик on 18.03.2017.
 */

public class ActualTrainingAdapter extends BaseAdapter {

    public interface OnActiveStepChangeListener {
        void onActiveStepChange(View view);
    }

    private OnActiveStepChangeListener activeStepChangeListener;
    private ProgramTraining programTraining;
    private List<ViewHolder> viewHolders = new ArrayList<>();
    private ViewHolder activeViewHolder;

    public ActualTrainingAdapter(OnActiveStepChangeListener listener) {
        activeStepChangeListener = listener;
    }

    @Override
    public int getCount() {
        return programTraining != null ? programTraining.childrenCount() : 0;
    }

    @Override
    public boolean isEnabled(int position) {
        return activeViewHolder.getPosition() != position;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewHolder vh = findViewHolder(convertView);

        if (vh == null) {
            View stepContent = inflater.inflate(R.layout.activity_actual_training_stepper_item, parent, false);
            vh = new ViewHolder(stepContent);
            viewHolders.add(vh);
        }

        vh.setPosition(position);
        if (activeViewHolder == null && position == 0)
            setActiveViewHolder(vh);
        vh.setTitle(programTraining.getChild(position).getExercise().getName());
        if (position == getCount() - 1)
            vh.setBottomLineVisibility(false);

        return vh.itemView;
    }

    public void setModel(ProgramTraining model) {
        programTraining = model;
        notifyDataSetChanged();
    }

    public void setActiveViewHolder(ViewHolder vh) {
        if (vh == null || (activeViewHolder != null && activeViewHolder.itemView == vh.itemView))
            return;

        if (activeViewHolder != null)
            activeViewHolder.setActive(false);

        activeViewHolder = vh;
        vh.setActive(true);
        activeStepChangeListener.onActiveStepChange(vh.itemView);
    }

    public ViewHolder findViewHolder(View view) {
        if (view == null)
            return null;

        for (ViewHolder vh : viewHolders) {
            if (vh.itemView == view)
                return vh;
        }

        return null;
    }

    static class ViewHolder {
        int position;
        TextView indexNumber;
        TextView title;
        TextView summary;
        View itemView;
        ImageView topVerticalLine;
        ImageView bottomVerticalLine;
        ImageView stepperCircle;

        public ViewHolder(View view) {
            itemView = view;
            indexNumber = (TextView) view.findViewById(R.id.textView4);
            topVerticalLine = (ImageView) view.findViewById(R.id.imageView4);
            bottomVerticalLine = (ImageView) view.findViewById(R.id.imageView3);
            stepperCircle = (ImageView) view.findViewById(R.id.imageView2);
            title = (TextView) view.findViewById(R.id.textView3);
            summary = (TextView) view.findViewById(R.id.textView5);
        }

        public void setPosition(int position) {
            this.position = position;
            indexNumber.setText(String.valueOf(position + 1));
            setTopLineVisibility(position != 0);
        }

        public int getPosition() {
            return position;
        }

        public void setTopLineVisibility(boolean visible) {
            topVerticalLine.setVisibility(visible ? View.VISIBLE : View.GONE);
        }

        public void setBottomLineVisibility(boolean visible) {
            bottomVerticalLine.setVisibility(visible ? View.VISIBLE : View.GONE);
        }

        public void setActive(boolean active) {
            @DrawableRes int imageId;
            @StyleRes int mainTextStyle;

            if (active) {
                imageId = R.drawable.ic_circle_primary_24dp;
                mainTextStyle = R.style.TextAppearance_AppCompat_Body2;
            } else {
                imageId = R.drawable.ic_circle_grey_24dp;
                mainTextStyle = R.style.TextAppearance_AppCompat_Small;
            }

            stepperCircle.setImageResource(imageId);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                title.setTextAppearance(itemView.getContext(), mainTextStyle);
            else
                title.setTextAppearance(mainTextStyle);
        }

        public void setTitle(String text) {
            title.setText(text);
        }

        public void setSummaryText(String text) {
            summary.setText(text);
            summary.setVisibility(View.VISIBLE);
        }
    }
}
