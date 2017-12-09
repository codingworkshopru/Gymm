package ru.codingworkshop.gymm.data.tree.saver;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.common.collect.Lists;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramTrainingAdapter;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.util.TreeBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.argThat;
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
        when(adapter.insertParent(any())).thenReturn(LiveDataUtil.getLive(1L));
        when(adapter.insertChildren(anyCollection())).thenReturn(LiveDataUtil.getLive(Lists.newArrayList(2L)));

        ProgramTrainingTree tree = TreeBuilders.buildProgramTrainingTree(1);
        tree.getParent().setId(0L);
        tree.getChildren().get(0).getParent().setId(0L);
        tree.getChildren().get(0).getChildren().get(0).setId(0L);

        saver.save(tree);

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
    public void save() throws Exception {
        when(adapter.getChildren(1L)).thenReturn(LiveDataUtil.getAbsent());
        when(adapter.getGrandchildren(1L)).thenReturn(LiveDataUtil.getAbsent());

        when(adapter.insertChildren(anyCollection())).thenReturn(LiveDataUtil.getLive(Lists.newArrayList(2L)));

        ProgramTrainingTree tree = TreeBuilders.buildProgramTrainingTree(1);
        saver.save(tree);
        verify(adapter).updateParent(any(ProgramTraining.class));
        verify(adapter).insertChildren(anyCollection());
        verify(adapter).insertGrandchildren(anyCollection());
    }
}