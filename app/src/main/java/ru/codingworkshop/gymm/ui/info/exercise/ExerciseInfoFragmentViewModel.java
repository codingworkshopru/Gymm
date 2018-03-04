package ru.codingworkshop.gymm.ui.info.exercise;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.tree.loader.ExerciseLoader;
import ru.codingworkshop.gymm.data.tree.node.ExerciseNode;
import ru.codingworkshop.gymm.data.util.LoaderAdapter;

/**
 * Created by Radik on 20.11.2017.
 */

public class ExerciseInfoFragmentViewModel extends ViewModel {
    private LoaderAdapter<ExerciseNode> adapter;

    @Inject
    ExerciseInfoFragmentViewModel(ExerciseLoader loader) {
        adapter = new LoaderAdapter<>(loader, new ExerciseNode());
    }

    public LiveData<ExerciseNode> load(long exerciseId) {
        return adapter.load(exerciseId);
    }

    @Override
    protected void onCleared() {
        if (adapter != null) {
            adapter.clear();
        }
    }
}
