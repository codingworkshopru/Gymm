package ru.codingworkshop.gymm.ui.program.training;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

    @Mock private ProgramTrainingRepository repository;
    @Mock private ExercisesRepository exercisesRepository;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        vm = new ProgramTrainingViewModel(repository, exercisesRepository);

        when(repository.getProgramTrainingById(1L)).thenReturn(Models.createLiveProgramTraining(1L, "foo", false));
        when(repository.getProgramExercisesForTraining(1L)).thenReturn(Models.createLiveProgramExercises(3));
        when(repository.getProgramSetsForTraining(1L)).thenReturn(Models.createLiveProgramSets(3));
        when(exercisesRepository.getExercisesForProgramTraining(1L)).thenReturn(Models.createLiveExercises("foobar", "bar", "baz"));
    }

    @Test
    public void load() {
        vm = new ProgramTrainingViewModel(repository, exercisesRepository);
        LiveTest.verifyLiveData(vm.load(1L), b -> {
            ProgramTrainingTree tree = vm.getProgramTrainingTree();
            assertEquals(1L, tree.getParent().getId());
            assertEquals(2L, tree.getChildren().get(0).getParent().getId());
            assertEquals(3L, tree.getChildren().get(0).getChildren().get(0).getId());

            return b;
        });

        verify(repository).getProgramTrainingById(1L);
        verify(repository).getProgramExercisesForTraining(1L);
        verify(repository).getProgramSetsForTraining(1L);
        verify(exercisesRepository).getExercisesForProgramTraining(1L);
    }

    @Test
    public void createWithDrafting() throws Exception {
        final LiveData<ProgramTraining> foo = Models.createLiveProgramTraining(1L, "foo", true);
        when(repository.getProgramTrainingById(1L)).thenReturn(foo);
        when(repository.getDraftingProgramTraining()).thenReturn(foo);
        LiveTest.verifyLiveData(vm.create(), created -> {
            assertTrue(vm.getProgramTrainingTree().getParent().isDrafting());
            verify(repository, never()).insertProgramTraining(any(ProgramTraining.class));

            return created;
        });
    }

    @Test
    public void createWithoutDrafting() throws Exception {
        when(repository.getDraftingProgramTraining()).thenReturn(LiveDataUtil.getLive(null));
        LiveTest.verifyLiveData(vm.create(), created -> {
            assertEquals(0L, vm.getProgramTrainingTree().getParent().getId());
            assertTrue(vm.getProgramTrainingTree().getParent().isDrafting());
            verify(repository).insertProgramTraining(any(ProgramTraining.class));

            return created;
        });
    }

    @Test
    public void save() throws Exception {
        when(repository.getProgramExercisesForTraining(any())).thenReturn(Models.createLiveProgramExercises(3));
        LiveTest.verifyLiveData(vm.load(1L), l -> {
            ProgramTrainingTree tree = vm.getProgramTrainingTree();
            tree.moveChild(0,1);

            vm.save();

            verify(repository).updateProgramTraining(tree.getParent());
            verify(repository).updateProgramExercises(argThat(toUpdate -> toUpdate.size() == 2));

            return l;
        });
    }

}