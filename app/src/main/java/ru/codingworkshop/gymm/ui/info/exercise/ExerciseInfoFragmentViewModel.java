package ru.codingworkshop.gymm.ui.info.exercise;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.tree.loader2.ExerciseLoader;
import ru.codingworkshop.gymm.data.tree.node.ExerciseNode;

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
            ExerciseNode node = new ExerciseNode();
            Flowable<ExerciseNode> flowable = loader.loadById(node, exerciseId);
            liveNode = LiveDataReactiveStreams.fromPublisher(flowable);
        }

        return liveNode;
    }
}
