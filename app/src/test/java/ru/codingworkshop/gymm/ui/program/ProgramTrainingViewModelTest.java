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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.tree.loader.ProgramTrainingTreeLoader;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramTrainingAdapter;
import ru.codingworkshop.gymm.data.tree.saver2.ProgramTrainingTreeSaver;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
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
            ProgramTrainingTree originalTree = invocation.getArgument(0);
            ProgramTrainingTree donorTree = TreeBuilders.buildProgramTrainingTree(1);
            originalTree.setParent(donorTree.getParent());
            originalTree.setChildren(donorTree.getChildren());
            return LiveDataUtil.getLive(originalTree);
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
    public void isProgramTrainingChanged() throws Exception {
        assertFalse(LiveTest.getValue(vm.isProgramTrainingChanged()));

        MutableProgramExerciseNode child = new MutableProgramExerciseNode(new ProgramExercise());
        ProgramTrainingTree tree = vm.getProgramTrainingTree();
        tree.addChild(child);
        assertTrue(LiveTest.getValue(vm.isProgramTrainingChanged()));
        tree.removeChild(0);

        tree.getParent().setName("baz");
        assertTrue(LiveTest.getValue(vm.isProgramTrainingChanged()));

        assertNotNull(LiveTest.getValue(vm.loadTree(1L)));

        when(adapter.getChildren(1L)).thenReturn(Models.createLiveProgramExercises(1));

        tree.getParent().setName("bar");
        assertTrue(LiveTest.getValue(vm.isProgramTrainingChanged()));
        tree.getParent().setName("foo");
        tree.addChild(child);
        assertTrue(LiveTest.getValue(vm.isProgramTrainingChanged()));
    }

    @Test
    public void createProgramExercise() throws Exception {
        ProgramTrainingTree tree = vm.getProgramTrainingTree();
        tree.getParent().setId(1L);

        vm.createProgramExercise();
        ProgramExerciseNode programExercise = vm.getProgramExercise().getValue();
        assertEquals(1L, programExercise.getProgramTrainingId());
        assertEquals(-1, programExercise.getSortOrder());
    }

    @Test
    public void saveProgramExercise() throws Exception {
        vm.createProgramExercise();
        vm.saveProgramExercise();

        assertNull(vm.getProgramExercise().getValue());
        int index = vm.getProgramTrainingTree().getChildrenCount() - 1;
        assertEquals(index, vm.getProgramTrainingTree().getChildren().get(index).getSortOrder());

        vm.setProgramExercise(vm.getProgramTrainingTree().getChildren().get(0));
        vm.saveProgramExercise();

        assertEquals(1, vm.getProgramTrainingTree().getChildrenCount());
    }

    @Test
    public void deleteProgramExercise() throws Exception {
        for (int i = 0; i < 3; i++) {
            vm.createProgramExercise();
            vm.saveProgramExercise();
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
        vm.saveProgramExercise();
        vm.deleteProgramExercise(0);
        vm.restoreLastDeletedProgramExercise();
        assertTrue(vm.getProgramTrainingTree().hasChildren());
    }

    @Test
    public void moveProgramExercise() throws Exception {
        vm.createProgramExercise();
        vm.saveProgramExercise();
        vm.createProgramExercise();
        vm.saveProgramExercise();
        List<ProgramExerciseNode> children = vm.getProgramTrainingTree().getChildren();
        ProgramExerciseNode e1 = children.get(0);
        ProgramExerciseNode e2 = children.get(1);
        vm.moveProgramExercise(1, 0);
        assertSame(e2, vm.getProgramTrainingTree().getChildren().get(0));
        assertSame(e1, vm.getProgramTrainingTree().getChildren().get(1));
    }

    @Test
    public void setProgramExercise() throws Exception {
        ProgramExerciseNode node = new MutableProgramExerciseNode();
        vm.setProgramExercise(node);
        ProgramExerciseNode setNode = vm.getProgramExercise().getValue();
        assertNotNull(setNode);
        assertNotSame(node, setNode);
    }

    @Test
    public void isProgramExerciseChanged() throws Exception {
        createProgramExercise();
        assertFalse(vm.isProgramExerciseChanged());

        ProgramExerciseNode node = vm.getProgramExercise().getValue();
        node.setExerciseId(100L);
        assertTrue(vm.isProgramExerciseChanged());

        node.setExerciseId(null);
        node.addChild(new ProgramSet());
        assertTrue(vm.isProgramExerciseChanged());

        LiveTest.verifyLiveData(vm.loadTree(1L), Objects::nonNull);
        vm.setProgramExercise(vm.getProgramTrainingTree().getChildren().get(0));

        assertFalse(vm.isProgramExerciseChanged());

        node = vm.getProgramExercise().getValue();
        node.setExerciseId(101L);
        assertTrue(vm.isProgramExerciseChanged());
        node.setExerciseId(100L);
        node.addChild(new ProgramSet());
        assertTrue(vm.isProgramExerciseChanged());
    }

    @Test
    public void createProgramSet() throws Exception {
        vm.createProgramExercise();
        vm.getProgramExercise().getValue().setId(2L);
        vm.createProgramSet();

        ProgramSet set = vm.getProgramSet().getValue();
        assertEquals(2L, set.getProgramExerciseId());
        assertEquals(-1, set.getSortOrder());
    }

    @Test
    public void saveProgramSet() throws Exception {
        vm.createProgramExercise();
        vm.createProgramSet();
        vm.saveProgramSet();

        assertNull(vm.getProgramSet().getValue());
        ProgramExerciseNode exercise = vm.getProgramExercise().getValue();
        int index = exercise.getChildrenCount() - 1;
        assertEquals(index, exercise.getChildren().get(index).getSortOrder());

        vm.setProgramSet(exercise.getChildren().get(0));
        vm.saveProgramSet();

        assertEquals(1, exercise.getChildrenCount());
    }

    @Test
    public void deleteProgramSet() throws Exception {
        vm.createProgramExercise();

        vm.createProgramSet();
        vm.saveProgramSet();
        vm.deleteProgramSet(0);
        assertFalse(vm.getProgramExercise().getValue().hasChildren());

        vm.createProgramSet();
        vm.saveProgramSet();
        vm.deleteProgramSet(vm.getProgramExercise().getValue().getChildren().get(0));
        assertFalse(vm.getProgramExercise().getValue().hasChildren());
    }

    @Test
    public void restoreLastDeletedProgramSet() throws Exception {
        vm.createProgramExercise();
        vm.createProgramSet();
        vm.saveProgramSet();
        vm.deleteProgramSet(0);
        vm.restoreLastDeletedProgramSet();
        assertTrue(vm.getProgramExercise().getValue().hasChildren());
    }

    @Test
    public void moveProgramSet() throws Exception {
        vm.createProgramExercise();
        vm.createProgramSet();
        vm.saveProgramSet();
        vm.createProgramSet();
        vm.saveProgramSet();

        ProgramExerciseNode node = vm.getProgramExercise().getValue();
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
        ProgramSet set = vm.getProgramSet().getValue();
        assertNotNull(set);
        assertNotSame(programSet, set);
    }
}