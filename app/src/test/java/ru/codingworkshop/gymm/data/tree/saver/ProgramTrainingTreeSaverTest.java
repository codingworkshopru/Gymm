package ru.codingworkshop.gymm.data.tree.saver;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.common.collect.Lists;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramTrainingAdapter;
import ru.codingworkshop.gymm.util.Models;
import ru.codingworkshop.gymm.util.TreeBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Radik on 29.11.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProgramTrainingTreeSaverTest {
    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ProgramTrainingAdapter adapter;
    @InjectMocks private ProgramTrainingTreeSaver saver;

    @Test
    public void saveNewTree() throws Exception {
        when(adapter.insertParent(any())).thenReturn(1L);
        when(adapter.insertChildren(anyCollection())).thenReturn(Collections.singletonList(2L));

        ProgramTrainingTree tree = TreeBuilders.buildProgramTrainingTree(1);
        tree.getParent().setId(0L);
        tree.getChildren().get(0).getParent().setId(0L);
        tree.getChildren().get(0).getChildren().get(0).setId(0L);

        saver.save(tree);

        final CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(adapter).insertGrandchildren(anyCollection());
        latch.await();

        verify(adapter).insertParent(tree.getParent());

        verify(adapter).insertChildren(argThat(ex -> {
            ProgramExercise exercise = ex.iterator().next();
            return exercise.getProgramTrainingId() == 1L;
        }));
        verify(adapter).insertGrandchildren(argThat(sets -> {
            ProgramSet set = sets.iterator().next();
            return set.getProgramExerciseId() == 2L;
        }));
    }

    @Test
    public void save() {
        when(adapter.getChildren(1L)).thenReturn(Flowable.just(Collections.emptyList()));
        when(adapter.getGrandchildren(1L)).thenReturn(Flowable.just(Collections.emptyList()));

        when(adapter.insertChildren(anyCollection())).thenReturn(Lists.newArrayList(2L));

        ProgramTrainingTree tree = new ImmutableProgramTrainingTree();
        tree.setParent(Models.createProgramTraining(1L, "foo"));
        ProgramExercise programExercise = new ProgramExercise();
        programExercise.setProgramTrainingId(1L);
        programExercise.setExerciseId(100L);
        ImmutableProgramExerciseNode programExerciseNode = new ImmutableProgramExerciseNode(programExercise);
        ProgramSet set = new ProgramSet();
        programExerciseNode.setChildren(Collections.singletonList(set));
        tree.setChildren(Collections.singletonList(programExerciseNode));

        saver.save(tree);

        verify(adapter).updateParent(any(ProgramTraining.class));
        verify(adapter).insertChildren(anyCollection());
        verify(adapter).insertGrandchildren(argThat(programSet -> programSet.iterator().next().getProgramExerciseId() == 2L));
    }
}