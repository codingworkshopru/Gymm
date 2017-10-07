package ru.codingworkshop.gymm.ui.program.training;


import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Preconditions;

import javax.inject.Inject;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import timber.log.Timber;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;
import static android.support.v7.widget.helper.ItemTouchHelper.DOWN;
import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;
import static android.support.v7.widget.helper.ItemTouchHelper.RIGHT;
import static android.support.v7.widget.helper.ItemTouchHelper.UP;

public class ProgramTrainingFragment extends Fragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private ProgramTrainingViewModel viewModel;
    private ProgramTrainingTree tree;
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (viewModel == null) {
            viewModel = viewModelFactory.create(ProgramTrainingViewModel.class);
        }
        tree = viewModel.getProgramTrainingTree();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_program_training, container, false);
        initExerciseList(rootView);
        return rootView;
    }

    private void initExerciseList(View rootView) {
        RecyclerView rv = rootView.findViewById(R.id.programExerciseList);
        ProgramExercisesAdapter adapter = new ProgramExercisesAdapter(context, tree.getChildren());
        rv.setAdapter(adapter);
        rv.setHasFixedSize(true);

        rv.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(UP | DOWN, LEFT | RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
                int from = source.getAdapterPosition();
                int to = target.getAdapterPosition();
                tree.moveChild(to, from);
                adapter.notifyItemMoved(from, to);
                return from != to;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int index = viewHolder.getAdapterPosition();
                tree.removeChild(index);
                adapter.notifyItemRemoved(index);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive)
            {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if (isCurrentlyActive && actionState == ACTION_STATE_SWIPE) {
                    Timber.d("onChildDraw: dX: %f", dX);
                    Drawable d = Preconditions.checkNotNull(
                            ContextCompat.getDrawable(context, R.drawable.ic_delete_black_24dp),
                            "Cannot create drawable from resource"
                    );
                    final int w = d.getIntrinsicWidth();
                    final int h = d.getIntrinsicHeight();
                    d.setBounds(0, 0, w, h);
                    Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                    Canvas imageCanvas = new Canvas(b);
                    d.draw(imageCanvas);

                    final View view = viewHolder.itemView;
                    final float top = view.getTop() + view.getHeight() / 2f - h / 2f;
                    if (dX > 0f) {
                        c.drawBitmap(b, w, top, null);
                    } else {
                        c.drawBitmap(b, view.getWidth() - 2*w, top, null);
                    }

                    b.recycle();
                }
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }
        });
        adapter.setReorderDownListener(v -> {
            final RecyclerView.ViewHolder vh = rv.findContainingViewHolder(v);
            if (vh != null) {
                itemTouchHelper.startDrag(vh);
            }
        });
        itemTouchHelper.attachToRecyclerView(rv);
    }

}
