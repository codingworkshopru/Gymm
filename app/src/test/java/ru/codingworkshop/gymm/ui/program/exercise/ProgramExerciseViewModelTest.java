package ru.codingworkshop.gymm.ui.program.exercise;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Objects;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.tree.loader.ProgramDraftingExerciseLoader;
import ru.codingworkshop.gymm.data.tree.loader.ProgramExerciseLoader;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;
import ru.codingworkshop.gymm.util.TreeBuilders;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 27.08.2017 as part of the Gymm project.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProgramExerciseViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ProgramExerciseLoader loader;
    @Mock private ProgramDraftingExerciseLoader draftingLoader;
    @Mock private ProgramTrainingRepository repository;
    private ProgramExerciseViewModel vm;

    @Before
    public void setUp() throws Exception {
        vm = new ProgramExerciseViewModel(loader, draftingLoader, repository);
        ProgramExerciseNode programExerciseNode = TreeBuilders.buildProgramTrainingTree(1).getChildren().get(0);
        when(loader.loadById(any(), eq(2L))).thenAnswer(invocation -> {
            vm.node = programExerciseNode;
            return LiveDataUtil.getLive(programExerciseNode);
        });
    }

    @Test
    public void load() throws Exception {
        LiveTest.verifyLiveData(vm.load(2L), Objects::nonNull);
        LiveTest.verifyLiveData(vm.load(0L), Objects::nonNull);

        verify(loader).loadById(any(), anyLong());
    }

    @Test
    public void createWithoutDrafting() throws Exception {
        vm.setProgramTrainingId(1L);
        when(draftingLoader.loadById(any(), eq(1L))).thenAnswer(invocation -> LiveDataUtil.getAbsent());
        LiveTest.verifyLiveData(vm.create(), Objects::nonNull);

        final ProgramExercise parent = vm.getProgramExerciseNode().getParent();
        assertEquals(0L, parent.getId());
        assertEquals(1L, parent.getProgramTrainingId());
        assertTrue(parent.isDrafting());
        verify(repository).insertProgramExercise(any());
    }

    @Test
    public void createWithDrafting() throws Exception {
        vm.setProgramTrainingId(1L);
        when(draftingLoader.loadById(any(), eq(1L))).thenAnswer(invocation -> {
            ProgramExerciseNode programExerciseNode = TreeBuilders.buildProgramTrainingTree(1).getChildren().get(0);
            programExerciseNode.getParent().setDrafting(true);
            vm.node = programExerciseNode;
            return LiveDataUtil.getLive(programExerciseNode);
        });
        LiveTest.verifyLiveData(vm.create(), Objects::nonNull);

        final ProgramExercise parent = vm.getProgramExerciseNode().getParent();
        assertEquals(2L, parent.getId());
        verify(repository, never()).insertProgramExercise(any());
    }

    @Test
    public void save() throws Exception {
        when(repository.getProgramSetsForExercise(any())).thenReturn(Models.createLiveProgramSets(2L, 1));
        LiveTest.verifyLiveData(vm.load(2L), Objects::nonNull);

        ProgramExerciseNode node = vm.getProgramExerciseNode();
        node.getChildren().get(0).setReps(1);
        node.addChild(Models.createProgramSet(4L, 2L, 10));
        vm.save();

        verify(repository).updateProgramExercise(node.getParent());
        verify(repository).updateProgramSets(any());
        verify(repository).insertProgramSets(any());
    }

    @Test
    public void deleteIfDraftingTest() throws Exception {
        LiveTest.verifyLiveData(vm.load(2L), Objects::nonNull);
        vm.deleteIfDrafting();
        verify(repository, never()).deleteProgramTraining(any());
        vm.getProgramExerciseNode().getParent().setDrafting(true);
        vm.deleteIfDrafting();
        verify(repository).deleteProgramExercise(argThat(t -> t.getId() == 2L));
    }

    @Test
    public void addChildTest() throws Exception {
        LiveTest.verifyLiveData(vm.load(2L), Objects::nonNull);

        ProgramSet programSet = Models.createProgramSet(3L, 2L, 10);

        vm.addProgramSet(programSet);
        ProgramSet actual = vm.getProgramExerciseNode().getChildren().get(2);
        assertSame(programSet, actual);
        assertEquals(2, actual.getSortOrder());
    }

    @Test
    public void replaceChildTest() throws Exception {
        LiveTest.verifyLiveData(vm.load(2L), Objects::nonNull);

        ProgramSet programSet = Models.createProgramSet(3L, 2L, 10);
        vm.replaceProgramSet(programSet);
        ProgramSet actual = vm.getProgramExerciseNode().getChildren().get(0);
        assertSame(programSet, actual);
    }

    @Test
    public void parentChangedTest() throws Exception {
        LiveTest.verifyLiveData(vm.load(2L), Objects::nonNull);
        vm.getProgramExerciseNode().getParent().setExerciseId(150L);
        assertTrue(vm.isChanged());
    }

    @Test
    public void childrenChangedTest() throws Exception {
        LiveTest.verifyLiveData(vm.load(2L), Objects::nonNull);
        vm.setChildrenChanged();
        assertTrue(vm.isChanged());
    }

    @Test
    public void parentAndChildrenChangedTest() throws Exception {
        LiveTest.verifyLiveData(vm.load(2L), Objects::nonNull);
        vm.getProgramExerciseNode().getParent().setExerciseId(150L);
        vm.setChildrenChanged();
        assertTrue(vm.isChanged());
    }

    @Test
    public void nothingChangedTest() throws Exception {
        LiveTest.verifyLiveData(vm.load(2L), Objects::nonNull);
        vm.getProgramExerciseNode().getParent().setExerciseId(100L);
        assertFalse(vm.isChanged());
    }
}
