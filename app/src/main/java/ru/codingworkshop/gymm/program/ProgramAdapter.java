package ru.codingworkshop.gymm.program;

import android.databinding.ObservableBoolean;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import ru.codingworkshop.gymm.data.model.Model;

/**
 * Created by Радик on 20.02.2017.
 */

public final class ProgramAdapter<VH extends ProgramViewHolder> extends RecyclerView.Adapter<VH> {

    public interface ListItemActionListener {
        void onListItemClick(View view);
        boolean onListItemLongClick(View view);
        boolean onMove(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target);
        void onSwiped(RecyclerView.ViewHolder viewHolder);
    }

    private Model.Parent mModel;
    private ItemTouchHelper mItemTouchHelper;
    private ListItemActionListener mListItemActionListener;
    public final ObservableBoolean mInEditMode = new ObservableBoolean();
    private ViewHolderFactory<VH> mViewHolderFactory;

    private static final String TAG = ProgramAdapter.class.getSimpleName();

    public ProgramAdapter(Model.Parent model, ListItemActionListener clickListener, ViewHolderFactory<VH> viewHolderFactory) {
        mModel = model;
        mListItemActionListener = clickListener;
        mViewHolderFactory = viewHolderFactory;
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return mListItemActionListener.onMove(viewHolder, target);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mListItemActionListener.onSwiped(viewHolder);
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }
        });
    }

    public void setModel(Model.Parent model) {
        mModel = model;
        notifyDataSetChanged();
    }

    public void setEditMode(boolean inEditMode) {
        mInEditMode.set(inEditMode);
    }

    public void attachItemTouchHelper(RecyclerView recyclerView) {
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        final VH viewHolder = mViewHolderFactory.createViewHolder(parent, this);
        View itemView = viewHolder.itemView;

        // on click and long click events are throwing to activity
        if (mListItemActionListener != null) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListItemActionListener.onListItemClick(v);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return mListItemActionListener.onListItemLongClick(v);
                }
            });
        }

        viewHolder.getReorderActionView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mItemTouchHelper.startDrag(viewHolder);
                }
                return false;
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Model child = mModel.getChild(position);
        holder.setModel(child);
    }

    @Override
    public int getItemCount() {
        return mModel != null ? mModel.childrenCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        return mModel.getChild(position).getId();
    }
}
