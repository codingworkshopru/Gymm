package ru.codingworkshop.gymm.program;

import android.databinding.ObservableBoolean;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import ru.codingworkshop.gymm.data.model.base.MutableModel;
import ru.codingworkshop.gymm.data.model.base.Parent;

/**
 * Created by Радик on 20.02.2017.
 */

public final class ProgramAdapter<VH extends ProgramViewHolder> extends RecyclerView.Adapter<VH> {
    private Parent mModel;
    private ItemTouchHelper mItemTouchHelper;
    private ListItemActionListener mListItemActionListener;
    private ViewHolderFactory<VH> mViewHolderFactory;

    public final ObservableBoolean mInEditMode = new ObservableBoolean();
    public final ObservableBoolean mIsEmpty = new ObservableBoolean(true);

    private static final String TAG = ProgramAdapter.class.getSimpleName();

    public ProgramAdapter(ListItemActionListener clickListener, ViewHolderFactory<VH> viewHolderFactory) {
        mListItemActionListener = clickListener;
        mViewHolderFactory = viewHolderFactory;
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int insertionIndex = target.getAdapterPosition();
                int sourceIndex = viewHolder.getAdapterPosition();
                mModel.moveChild(sourceIndex, insertionIndex);
                notifyItemMoved(sourceIndex, insertionIndex);

                mListItemActionListener.onMove(viewHolder, target);

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int viewHolderPosition = viewHolder.getAdapterPosition();
                mModel.removeChild(viewHolderPosition);
                notifyItemRemoved(viewHolderPosition);

                mListItemActionListener.onSwiped(viewHolder);
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }
        });
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() { setEmptyState(); }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) { setEmptyState(); }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) { setEmptyState(); }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) { setEmptyState(); }
        });
    }

    public void setModel(Parent model) {
        mModel = model;
        notifyDataSetChanged();
    }

    public void setEditMode(boolean inEditMode) {
        mInEditMode.set(inEditMode);
    }

    private void setEmptyState() {
        mIsEmpty.set(mModel == null || mModel.childrenCount() == 0);
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
        MutableModel child = mModel.getChild(position);
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

    public interface ListItemActionListener {
        void onListItemClick(View view);
        boolean onListItemLongClick(View view);
        void onMove(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target);
        void onSwiped(RecyclerView.ViewHolder viewHolder);
    }
}
