package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.TreeBuilders;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Created by Radik on 17.11.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ActualTrainingEmptyTreeLoaderTest {
    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ProgramTrainingTreeLoader programTrainingTreeLoader;
    private ActualTrainingEmptyTreeLoader loader;

    @Before
    public void setUp() throws Exception {
        when(programTrainingTreeLoader.loadById(any(), eq(1L)))
                .thenReturn(LiveDataUtil.getLive(TreeBuilders.buildProgramTrainingTree(1)));

        loader = new ActualTrainingEmptyTreeLoader(programTrainingTreeLoader);
    }

    @Test
    public void loadById() throws Exception {
        LiveTest.verifyLiveData(loader.loadById(new ActualTrainingTree(), 1L), tree -> {

            assertNotNull(tree.getParent());
            assertNotNull(tree.getProgramTraining());
            assertEquals(1, tree.getChildrenCount());
            assertNotNull(tree.getChildren().get(0).getProgramExerciseNode());

            return true;
        });
    }
}