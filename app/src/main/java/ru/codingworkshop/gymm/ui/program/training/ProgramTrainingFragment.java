package ru.codingworkshop.gymm.ui.program.training;


import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.tree.ChildRestoreAdapter;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.ui.program.common.MyAdapterDataObserver;
import ru.codingworkshop.gymm.ui.program.common.MySimpleCallback;

public class ProgramTrainingFragment extends Fragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private ProgramTrainingViewModel viewModel;
    private ProgramTrainingTree tree;
    private Context context;
    private ObservableBoolean inActionMode = new ObservableBoolean(false);

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
        rootView.findViewById(R.id.programTrainingBackground).setVisibility(View.GONE);
        return rootView;
    }

    private void initExerciseList(View rootView) {
        RecyclerView rv = rootView.findViewById(R.id.programExerciseList);
        ProgramExercisesAdapter adapter = new ProgramExercisesAdapter(context, tree.getChildren(), inActionMode);
        adapter.registerAdapterDataObserver(new MyAdapterDataObserver(rv, R.id.programTrainingBackground, R.string.program_training_activity_exercise_deleted_message) {
            @Override
            public void restoreLastRemoved() {
                new ChildRestoreAdapter(tree).restoreLastRemoved();
            }
        });
        rv.setAdapter(adapter);
        rv.setHasFixedSize(true);

        rv.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new MySimpleCallback(rv) {
            @Override
            public void moveChild(int from, int to) {
                tree.moveChild(from, to);
            }

            @Override
            public void removeChild(int index) {
                tree.removeChild(index);
            }
        });
        adapter.setLongClickListener(v -> {
            getActivity().startActionMode(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    itemTouchHelper.attachToRecyclerView(rv);
                    inActionMode.set(true);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    itemTouchHelper.attachToRecyclerView(null);
                    inActionMode.set(false);
                }
            });
            return true;
        });
        adapter.setReorderDownListener(v -> {
            final RecyclerView.ViewHolder vh = rv.findContainingViewHolder(v);
            if (vh != null) {
                itemTouchHelper.startDrag(vh);
            }
        });
    }

}
