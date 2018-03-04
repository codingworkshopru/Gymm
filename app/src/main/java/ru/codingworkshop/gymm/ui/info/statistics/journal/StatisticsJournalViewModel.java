package ru.codingworkshop.gymm.ui.info.statistics.journal;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.tree.loader.ImmutableActualTrainingTreeLoader;
import ru.codingworkshop.gymm.data.tree.loader.common.Loader;
import ru.codingworkshop.gymm.data.tree.node.ImmutableActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ImmutableActualTrainingTree;
import ru.codingworkshop.gymm.repository.ActualTrainingRepository;


public class StatisticsJournalViewModel extends ViewModel {
    private ActualTrainingRepository repository;
    private Loader<ImmutableActualTrainingTree> loader;

    private LiveData<List<ActualTraining>> actualTrainings;
    private ImmutableActualExerciseNode currentExerciseNode;
    private ImmutableActualTrainingTree actualTrainingTree;

    @Inject
    StatisticsJournalViewModel(ActualTrainingRepository repository, ImmutableActualTrainingTreeLoader loader) {
        this.repository = repository;
        this.loader = loader;
    }

    public LiveData<List<ActualTraining>> getActualTrainings() {
        if (actualTrainings == null) {
            actualTrainings = repository.getActualTrainings();
        }
        return actualTrainings;
    }

    public LiveData<ImmutableActualTrainingTree> loadTree(long actualTrainingId) {
        actualTrainingTree = new ImmutableActualTrainingTree();
        return LiveDataReactiveStreams.fromPublisher(loader.loadById(actualTrainingTree, actualTrainingId));
    }

    public ImmutableActualTrainingTree getActualTrainingTree() {
        return actualTrainingTree;
    }

    public void setCurrentExerciseNode(ImmutableActualExerciseNode node) {
        currentExerciseNode = node;
    }

    public ImmutableActualExerciseNode getCurrentExerciseNode() {
        return currentExerciseNode;
    }
}
