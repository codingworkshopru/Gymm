package ru.codingworkshop.gymm;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.lang.reflect.Constructor;

import ru.codingworkshop.gymm.data.model.Model;

/**
 * Created by Радик on 20.02.2017.
 */

final class ProgramAdapter<VH extends ProgramAdapter.ProgramViewHolder> extends RecyclerView.Adapter<VH> {
    private Model.Parent mModel;
    private ItemTouchHelper mItemTouchHelper;
    private Class<VH> mViewHolderType;
    private View.OnClickListener mClickListener;
    private View.OnLongClickListener mLongClickListener;
    private boolean mInEditMode = false;

    private static final String TAG = ProgramAdapter.class.getSimpleName();

    ProgramAdapter(Model.Parent model, Class<VH> viewHolderType) {
        mModel = model;
        mViewHolderType = viewHolderType;
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int insertionIndex = target.getAdapterPosition();
                int sourceIndex = viewHolder.getAdapterPosition();
                mModel.moveChild(sourceIndex, insertionIndex);
                ProgramAdapter.this.notifyItemMoved(sourceIndex, insertionIndex);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int viewHolderPosition = viewHolder.getAdapterPosition();
                mModel.removeChild(viewHolderPosition);
                ProgramAdapter.this.notifyItemRemoved(viewHolderPosition);
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }
        });
    }

    ProgramAdapter(Model.Parent model, Class<VH> viewHolderType, View.OnClickListener clickListener) {
        this(model, viewHolderType);
        mClickListener = clickListener;
    }

    ProgramAdapter(Model.Parent model, Class<VH> viewHolderType, View.OnClickListener clickListener, View.OnLongClickListener longClickListener) {
        this(model, viewHolderType, clickListener);
        mLongClickListener = longClickListener;
    }

    void attachItemTouchHelperToRecyclerView(RecyclerView recyclerView) {
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    void setModel(Model.Parent model) {
        mModel = model;
        notifyDataSetChanged();
    }

    void setEditMode(boolean inEditMode) {
        mInEditMode = inEditMode;
//        notifyDataSetChanged();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.activity_program_exercise_list_item, parent, false);

        // onClick
        if (mClickListener != null)
            view.setOnClickListener(mClickListener);

        if (mLongClickListener != null)
            view.setOnLongClickListener(mLongClickListener);

        try {
            Constructor<VH> constructor = mViewHolderType.getDeclaredConstructor(View.class);
            constructor.setAccessible(true);
            final VH viewHolder = constructor.newInstance(view);

            // for items movement
            ImageView imageView = (ImageView) view.findViewById(R.id.program_exercise_list_item_reorder_action);
            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        mItemTouchHelper.startDrag(viewHolder);
                    }
                    return false;
                }
            });

            return viewHolder;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Model child = mModel.getChild(position);
        holder.setModel(child);

        View view = holder.itemView.findViewById(R.id.program_exercise_list_item_delete_sweep);
        View view2 = holder.itemView.findViewById(R.id.program_exercise_list_item_reorder_action);
//        if (view != null) {
            if (mInEditMode) {
                view.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
//                Animation animation = AnimationUtils.makeInAnimation(view.getContext(), false);
//                animation.setDuration(500);
//                animation.setFillAfter(true);
//                view.startAnimation(animation);
//                view2.startAnimation(animation);
            } else {
//                if (view.getVisibility() == View.VISIBLE) {
//                    Animation animation = AnimationUtils.makeOutAnimation(view.getContext(), true);
//                    animation.setDuration(500);
//                    animation.setFillAfter(true);
//                    view.setAnimation(animation);
//                    view2.setAnimation(animation);
                    view.setVisibility(View.GONE);
                    view2.setVisibility(View.GONE);
                }
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return mModel != null ? mModel.childrenCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        return mModel.getChild(position).getId();
    }

    public static abstract class ProgramViewHolder<T extends Model> extends RecyclerView.ViewHolder {
        T mModel;

        ProgramViewHolder(View itemView) {
            super(itemView);
        }

        public void setModel(T model) {
            mModel = model;
        }

        public T getModel() {
            return mModel;
        }
    }
}
