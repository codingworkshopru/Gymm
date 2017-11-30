package ru.codingworkshop.gymm.data.tree.saver2;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramTrainingAdapter;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.util.TreeBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
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
    public void save() throws Exception {
        when(adapter.getChildren(1L)).thenReturn(LiveDataUtil.getAbsent());
        when(adapter.getGrandchildren(1L)).thenReturn(LiveDataUtil.getAbsent());

        ProgramTrainingTree tree = TreeBuilders.buildProgramTrainingTree(1);
        saver.save(tree);
        verify(adapter).updateParent(any(ProgramTraining.class));
        verify(adapter).insertChildren(anyCollection());
        verify(adapter).insertGrandchildren(anyCollection());
    }
}