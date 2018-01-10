package ru.codingworkshop.gymm.ui.info.statistics.journal;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.tree.loader.ImmutableActualTrainingTreeLoader;
import ru.codingworkshop.gymm.data.tree.node.ImmutableActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ImmutableActualTrainingTree;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.repository.ActualTrainingRepository;
import ru.codingworkshop.gymm.util.LiveTest;

import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StatisticsJournalViewModelTest {
    @Mock private ActualTrainingRepository repository;
    @Mock private ImmutableActualTrainingTreeLoader loader;
    @InjectMocks private StatisticsJournalViewModel vm;

    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void getActualTrainings() {
        when(vm.getActualTrainings())
                .thenReturn(LiveDataUtil.getAbsent())
                .thenReturn(LiveDataUtil.getAbsent());

        LiveData<List<ActualTraining>> trainings = vm.getActualTrainings();
        assertSame(trainings, vm.getActualTrainings());

        verify(repository).getActualTrainings();
    }

    @Test
    public void loadTree() {
        final ImmutableActualTrainingTree tree = new ImmutableActualTrainingTree();
        when(loader.loadById(any(), eq(11L))).thenReturn(Flowable.just(tree));
        LiveTest.verifyLiveData(vm.loadTree(11L), t -> t == tree);
    }

    @Test
    public void setActualExerciseNode() {
        final ImmutableActualExerciseNode node = new ImmutableActualExerciseNode();
        vm.setCurrentExerciseNode(node);
        assertSame(node, vm.getCurrentExerciseNode());
    }
}