package ru.codingworkshop.gymm.ui.program.exercise;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Objects;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
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

    @Mock private ProgramTrainingRepository repository;
    @Mock private ExercisesRepository exercisesRepository;
    private ProgramExerciseViewModel vm;

    @Before
    public void setUp() throws Exception {
        vm = new ProgramExerciseViewModel(repository, exercisesRepository);
        when(repository.getProgramExerciseById(2L)).thenReturn(Models.createLiveProgramExercise(2L,1L,false));
        when(repository.getProgramSetsForExercise(2L)).thenReturn(Models.createLiveProgramSets(2L, 1));
        when(exercisesRepository.getExerciseById(100L)).thenReturn(Models.createLiveExercise(100L, "foo"));
    }

    @Test
    public void load() throws Exception {
        LiveTest.verifyLiveData(vm.load(2L), node -> {
            assertEquals(2L, node.getId());

            return true;
        });
    }

    @Test
    public void createWithoutDrafting() throws Exception {
        when(repository.getDraftingProgramExercise(1L)).thenReturn(LiveDataUtil.getLive(null));
        vm.setProgramTrainingId(1L);
        LiveTest.verifyLiveData(vm.create(), node -> {
            final ProgramExercise parent = node.getParent();
            assertEquals(0L, parent.getId());
            assertEquals(1L, parent.getProgramTrainingId());
            assertTrue(parent.isDrafting());
            verify(repository).insertProgramExercise(any());

            return true;
        });
    }

    @Test
    public void createWithDrafting() throws Exception {
        final LiveData<ProgramExercise> liveProgramExercise = Models.createLiveProgramExercise(2L, 1L, true);
        when(repository.getProgramExerciseById(2L)).thenReturn(liveProgramExercise);
        when(repository.getDraftingProgramExercise(1L)).thenReturn(liveProgramExercise);

        vm.setProgramTrainingId(1L);
        LiveTest.verifyLiveData(vm.create(), node -> {
            final ProgramExercise parent = node.getParent();
            assertEquals(2L, parent.getId());
            assertTrue(parent.isDrafting());
            verify(repository, never()).insertProgramExercise(any());

            return true;
        });
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
        ProgramSet actual = vm.getProgramExerciseNode().getChildren().get(1);
        assertSame(programSet, actual);
        assertEquals(1, actual.getSortOrder());
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
