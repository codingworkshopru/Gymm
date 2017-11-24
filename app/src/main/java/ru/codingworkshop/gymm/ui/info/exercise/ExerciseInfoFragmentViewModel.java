package ru.codingworkshop.gymm.ui.info.exercise;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.tree.loader.ExerciseLoader;
import ru.codingworkshop.gymm.data.tree.node.ExerciseNode;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;

/**
 * Created by Radik on 20.11.2017.
 */

public class ExerciseInfoFragmentViewModel extends ViewModel {
    private ExerciseLoader loader;
    private LiveData<ExerciseNode> liveNode;

    @Inject
    ExerciseInfoFragmentViewModel(ExerciseLoader loader) {
        this.loader = loader;
    }

    public LiveData<ExerciseNode> getLiveNode() {
        return liveNode;
    }

    public LiveData<ExerciseNode> load(long exerciseId) {
        if (liveNode == null) {
            liveNode = loader.loadById(new ExerciseNode(), exerciseId);
        }

        return liveNode;
    }
}
