package ru.codingworkshop.gymm.ui.program.training;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.text.TextUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Objects;

import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.loader.ProgramDraftingTrainingTreeLoader;
import ru.codingworkshop.gymm.data.tree.loader.ProgramTrainingTreeLoader;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;
import ru.codingworkshop.gymm.util.TreeBuilders;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 20.08.2017 as part of the Gymm project.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProgramTrainingViewModelTest {
    private ProgramTrainingViewModel vm;

    @Mock private ProgramTrainingTreeLoader loader;
    @Mock private ProgramDraftingTrainingTreeLoader draftingLoader;
    @Mock private ProgramTrainingRepository repository;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        vm = new ProgramTrainingViewModel(loader, draftingLoader, repository);

        when(loader.loadById(any(), anyLong())).thenAnswer(invocation -> {
            ProgramTrainingTree tree = TreeBuilders.buildProgramTrainingTree(3);
            vm.tree = tree;
            return LiveDataUtil.getLive(tree);
        });
    }

    @Test
    public void load() {
        LiveTest.verifyLiveData(vm.load(1L), tree -> {
            assertEquals(1L, tree.getParent().getId());
            assertEquals(2L, tree.getChildren().get(0).getParent().getId());
            assertEquals(3L, tree.getChildren().get(0).getChildren().get(0).getId());

            return true;
        });

        LiveTest.verifyLiveData(vm.load(0L), Objects::nonNull);

        verify(loader).loadById(any(), anyLong());
    }

    @Test
    public void createWithDrafting() throws Exception {
        when(draftingLoader.load(any())).thenAnswer(invocation -> {
            ProgramTrainingTree tree = TreeBuilders.buildProgramTrainingTree(1);
            tree.getParent().setDrafting(true);
            vm.tree = tree;

            return LiveDataUtil.getLive(tree);
        });
        LiveTest.verifyLiveData(vm.create(), Objects::nonNull);

        ProgramTraining programTraining = vm.getProgramTrainingTree().getParent();
        assertEquals(1L, programTraining.getId());
        assertTrue(programTraining.isDrafting());
        assertTrue(vm.getProgramTrainingTree().hasChildren());
        assertTrue(vm.getProgramTrainingTree().getChildren().get(0).hasChildren());

        verify(repository, never()).insertProgramTraining(any(ProgramTraining.class));
    }

    @Test
    public void createWithoutDrafting() throws Exception {
        when(draftingLoader.load(any())).thenReturn(LiveDataUtil.getAbsent());
        LiveTest.verifyLiveData(vm.create(), Objects::isNull);

        ProgramTrainingTree tree = vm.getProgramTrainingTree();
        assertEquals(0L, tree.getParent().getId());
        assertTrue(tree.getParent().isDrafting());
        verify(repository).insertProgramTraining(any(ProgramTraining.class));
    }

    @Test
    public void saveWithNonUniqueName() throws Exception {
        when(repository.getProgramTrainingByName("foo")).thenReturn(Models.createLiveProgramTraining(2L, "foo", false));
        LiveTest.verifyLiveData(vm.load(1L), Objects::nonNull);
        LiveData<Boolean> savedLive = vm.save();
        LiveTest.verifyLiveData(savedLive, saved -> !saved);
        verify(repository, never()).updateProgramTraining(any());
    }

    @Test
    public void save() throws Exception {
        when(repository.getProgramTrainingByName("foo")).thenReturn(LiveDataUtil.getAbsent());
        when(repository.getProgramExercisesForTraining(any())).thenReturn(Models.createLiveProgramExercises(3));
        LiveTest.verifyLiveData(vm.load(1L), Objects::nonNull);

        ProgramTrainingTree tree = vm.getProgramTrainingTree();
        tree.moveChild(0,1);

        LiveTest.verifyLiveData(vm.save(), s -> s);
        assertFalse(tree.getParent().isDrafting());

        verify(repository).updateProgramTraining(tree.getParent());
        verify(repository).updateProgramExercises(argThat(toUpdate -> toUpdate.size() == 2));
    }

    @Test
    public void deleteIfDraftingTest() throws Exception {
        LiveTest.verifyLiveData(vm.load(1L), Objects::nonNull);
        vm.deleteIfDrafting();
        verify(repository, never()).deleteProgramTraining(any());
        vm.getProgramTrainingTree().getParent().setDrafting(true);
        vm.deleteIfDrafting();
        verify(repository).deleteProgramTraining(argThat(t -> t.getId() == 1L));
    }

    @Test
    public void parentChangedTest() throws Exception {
        LiveTest.verifyLiveData(vm.load(1L), Objects::nonNull);
        vm.getProgramTrainingTree().getParent().setName("bar");
        assertTrue(vm.isChanged());
    }

    @Test
    public void childrenChangedTest() throws Exception {
        LiveTest.verifyLiveData(vm.load(1L), Objects::nonNull);
        vm.setChildrenChanged();
        assertTrue(vm.isChanged());
    }

    @Test
    public void parentAndChildrenChangedTest() throws Exception {
        LiveTest.verifyLiveData(vm.load(1L), Objects::nonNull);
        vm.getProgramTrainingTree().getParent().setName("bar");
        vm.setChildrenChanged();
        assertTrue(vm.isChanged());
    }

    @Test
    public void nothingChangedTest() throws Exception {
        LiveTest.verifyLiveData(vm.load(1L), Objects::nonNull);
        vm.getProgramTrainingTree().getParent().setName("foo");
        assertFalse(vm.isChanged());
    }
}