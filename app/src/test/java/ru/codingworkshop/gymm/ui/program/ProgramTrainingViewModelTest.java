package ru.codingworkshop.gymm.ui.program;

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
import java.util.Objects;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.tree.loader.ProgramTrainingTreeLoader;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramTrainingAdapter;
import ru.codingworkshop.gymm.data.tree.saver.Saver;
import ru.codingworkshop.gymm.data.tree.saver2.ProgramTrainingTreeSaver;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;
import ru.codingworkshop.gymm.util.TreeBuilders;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Radik on 28.11.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProgramTrainingViewModelTest {
    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ProgramTrainingTreeSaver saver;
    @Mock private ProgramTrainingTreeLoader loader;
    @Mock private ProgramTrainingAdapter adapter;
    @InjectMocks private ProgramTrainingViewModel vm;

    @Before
    public void setUp() throws Exception {
        when(loader.loadById(any(), anyLong())).thenAnswer(invocation -> {
            ProgramTrainingTree tree = invocation.getArgument(0);
            return LiveDataUtil.getLive(tree);
        });
    }

    @Test
    public void initializationTest() throws Exception {
        ProgramTrainingTree tree = vm.getProgramTrainingTree();

        assertNotNull(tree);
        assertEquals(MutableProgramTrainingTree.class, tree.getClass());

        assertNotNull(tree.getParent());
        assertFalse(tree.hasChildren());
    }

    @Test
    public void loadTree() throws Exception {
        LiveData<ProgramTrainingTree> liveTree = vm.loadTree(1L);
        LiveTest.verifyLiveData(liveTree, tree -> tree != null && tree == vm.getProgramTrainingTree());
        verify(loader).loadById(any(), eq(1L));
    }

    @Test
    public void saveTree() throws Exception {
        vm.saveTree();
        verify(saver).save(any());
    }

    @Test
    public void validateProgramTraining() throws Exception {
        when(adapter.getProgramTrainingByName("foo"))
                .thenReturn(Models.createLiveProgramTraining(1L, "foo", false))
                .thenReturn(LiveDataUtil.getAbsent());

        vm.getProgramTrainingTree().setParent(Models.createProgramTraining(1L, "foo"));

        LiveTest.verifyLiveData(vm.validateProgramTraining(), isValid -> isValid);
        LiveTest.verifyLiveData(vm.validateProgramTraining(), isValid -> isValid);
    }

    @Test
    public void createProgramExercise() throws Exception {
        ProgramTrainingTree tree = vm.getProgramTrainingTree();
        tree.getParent().setId(1L);

        int index = vm.createProgramExercise();
        assertEquals(0, index);
        LiveTest.verifyLiveData(vm.getProgramExercise(), e -> 1L == e.getProgramTrainingId());

        index = vm.createProgramExercise();
        assertEquals(1, index);
        LiveTest.verifyLiveData(vm.getProgramExercise(), e -> 1L == e.getProgramTrainingId());

        assertEquals(2, tree.getChildrenCount());
    }

    @Test
    public void deleteProgramExercise() throws Exception {
        for (int i = 0; i < 3; i++) {
            vm.createProgramExercise();
        }

        ProgramTrainingTree tree = vm.getProgramTrainingTree();

        vm.deleteProgramExercise(0);
        assertEquals(2, tree.getChildrenCount());

        vm.deleteProgramExercise(tree.getChildren().get(1).getParent());
        assertEquals(1, tree.getChildrenCount());

        vm.deleteProgramExercise(tree.getChildren().get(0));

        assertFalse(tree.hasChildren());
    }

    @Test
    public void restoreLastDeletedProgramExercise() throws Exception {
        vm.createProgramExercise();
        vm.deleteProgramExercise(0);
        vm.restoreLastDeletedProgramExercise();
        assertTrue(vm.getProgramTrainingTree().hasChildren());
    }

    @Test
    public void moveProgramExercise() throws Exception {
        vm.createProgramExercise();
        vm.createProgramExercise();
        List<ProgramExerciseNode> children = vm.getProgramTrainingTree().getChildren();
        ProgramExerciseNode e1 = children.get(0);
        ProgramExerciseNode e2 = children.get(1);
        vm.moveProgramExercise(1, 0);
        assertSame(e2, vm.getProgramTrainingTree().getChildren().get(0));
        assertSame(e1, vm.getProgramTrainingTree().getChildren().get(1));
    }

    @Test
    public void setProgramExercise() throws Exception {
        ProgramExerciseNode node = new ImmutableProgramExerciseNode();
        vm.setProgramExercise(node);
        LiveTest.verifyLiveData(vm.getProgramExercise(), resultNode -> resultNode == node);
    }

    @Test
    public void createProgramSet() throws Exception {
        vm.createProgramExercise();
        vm.getProgramExercise().getValue().setId(2L);

        int index = vm.createProgramSet();
        assertEquals(0, index);
        LiveTest.verifyLiveData(vm.getProgramSet(), set -> set.getProgramExerciseId() == 2L);

        index = vm.createProgramSet();
        assertEquals(1, index);
        LiveTest.verifyLiveData(vm.getProgramSet(), set -> set.getProgramExerciseId() == 2L);

        assertEquals(2, vm.getProgramExercise().getValue().getChildrenCount());
    }

    @Test
    public void deleteProgramSet() throws Exception {
        vm.createProgramExercise();

        vm.createProgramSet();
        vm.deleteProgramSet(0);
        assertFalse(vm.getProgramTrainingTree().getChildren().get(0).hasChildren());

        vm.createProgramSet();
        vm.deleteProgramSet(vm.getProgramTrainingTree().getChildren().get(0).getChildren().get(0));
        assertFalse(vm.getProgramTrainingTree().getChildren().get(0).hasChildren());
    }

    @Test
    public void restoreLastDeletedProgramSet() throws Exception {
        vm.createProgramExercise();
        vm.createProgramSet();
        vm.deleteProgramSet(0);
        vm.restoreLastDeletedProgramSet();
        assertTrue(vm.getProgramTrainingTree().hasChildren());
    }

    @Test
    public void moveProgramSet() throws Exception {
        vm.createProgramExercise();
        vm.createProgramSet();
        vm.createProgramSet();

        ProgramExerciseNode node = vm.getProgramTrainingTree().getChildren().get(0);
        ProgramSet set1 = node.getChildren().get(0);
        ProgramSet set2 = node.getChildren().get(1);

        vm.moveProgramSet(1, 0);
        assertSame(set1, node.getChildren().get(1));
        assertSame(set2, node.getChildren().get(0));
    }

    @Test
    public void setProgramSet() throws Exception {
        ProgramSet programSet = new ProgramSet();
        vm.setProgramSet(programSet);
        LiveTest.verifyLiveData(vm.getProgramSet(), set -> set == programSet);
    }

}