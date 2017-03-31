package ru.codingworkshop.gymm.program.activity.exercise.picker.exercises;

import android.database.Cursor;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.codingworkshop.gymm.R;

/**
 * Created by Радик on 30.03.2017.
 */
class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ViewHolder> {
    private Cursor cursor;
    private static final int FIRST_DIVIDER_POSITION = 0;
    private int secondDividerPosition = -1;
    private OnItemClickListener itemClickListener;

    private static final String TAG = ExercisesAdapter.class.getSimpleName();
    private static final int TOP_DIVIDER_VIEW_TYPE = 1;
    private static final int DIVIDER_VIEW_TYPE = 2;

    interface OnItemClickListener {
        void onItemClick(View view);
        void onInfoButtonClick(View view);
    }

    ExercisesAdapter(OnItemClickListener listener) {
        itemClickListener = listener;
        setHasStableIds(true);
    }

    public void setCursor(Cursor newCursor) {
        if (cursor != null)
            cursor.close();

        cursor = newCursor;

        cursor.moveToFirst();
        cursor.moveToPrevious();

        while (cursor.moveToNext()) {
            if (cursor.getLong(0) == 0 && cursor.getPosition() > 0 && !cursor.isLast()) {
                secondDividerPosition = cursor.getPosition();
                break;
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        cursor.moveToPosition(position);
        return cursor.getLong(0);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        @LayoutRes int layoutId = R.layout.activity_exercises_list_item;

        if (viewType == DIVIDER_VIEW_TYPE || viewType == TOP_DIVIDER_VIEW_TYPE)
            layoutId = R.layout.list_subheader_item;

        final View itemView = inflater.inflate(layoutId, parent, false);

        if (viewType == 0) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(v);
                }
            });

            itemView.findViewById(R.id.imageButton4).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onInfoButtonClick(itemView);
                }
            });
        }

        return new ViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == FIRST_DIVIDER_POSITION)
            return TOP_DIVIDER_VIEW_TYPE;
        if (position == secondDividerPosition)
            return DIVIDER_VIEW_TYPE;

        return super.getItemViewType(position);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);

        if (getItemViewType(position) == TOP_DIVIDER_VIEW_TYPE) {
            holder.setSubheader("Основные упражнения"); // FIXME перенести в ресурс
        } else if (getItemViewType(position) == DIVIDER_VIEW_TYPE) {
            holder.setSubheader("В которых задействована");
        } else {
            holder.setText(cursor.getString(1));
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;

        if (cursor != null && cursor.getCount() > 0) {
            count = cursor.getCount();
            if (secondDividerPosition == -1)
                count--;
        }

        return count;
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
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
