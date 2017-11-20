package ru.codingworkshop.gymm.ui.info.exercise;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import ru.codingworkshop.gymm.data.tree.loader.ExerciseLoader;
import ru.codingworkshop.gymm.data.tree.node.ExerciseNode;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;

/**
 * Created by Radik on 20.11.2017.
 */

public class ExerciseInfoFragmentViewModel extends ViewModel {
    private ExerciseLoader loader;
    private ExerciseNode node;

    public ExerciseInfoFragmentViewModel(ExerciseLoader loader) {
        this.loader = loader;
    }

    public LiveData<ExerciseNode> load(long exerciseId) {
        if (node == null) {
            node = new ExerciseNode();
            return loader.loadById(node, exerciseId);
        } else {
            return LiveDataUtil.getLive(node);
        }
    }
}
