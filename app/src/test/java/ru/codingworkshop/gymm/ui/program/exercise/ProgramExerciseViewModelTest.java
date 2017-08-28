package ru.codingworkshop.gymm.ui.program.exercise;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
        when(repository.getProgramSetsForExercise(2L)).thenReturn(Models.createLiveProgramSets(1));
        when(exercisesRepository.getExerciseById(100L)).thenReturn(Models.createLiveExercise(100L, "foo"));
    }

    @Test
    public void load() throws Exception {
        LiveTest.verifyLiveData(vm.load(2L), loaded -> {
            ProgramExerciseNode node = vm.getProgramExerciseNode();
            assertEquals(2L, node.getId());

            return loaded;
        });
    }

    @Test
    public void create() throws Exception {
        vm.create();
        ProgramExerciseNode node = vm.getProgramExerciseNode();
        assertTrue(node.getParent().isDrafting());
        verify(repository).insertProgramExercise(any());
    }

    @Test
    public void save() throws Exception {
        when(repository.getProgramSetsForExercise(any())).thenReturn(Models.createLiveProgramSets(1));
        LiveTest.verifyLiveData(vm.load(2L), loaded -> {
            ProgramExerciseNode node = vm.getProgramExerciseNode();
            node.getChildren().get(0).setReps(1);
            node.addChild(Models.createProgramSet(4L, 2L, 10));
            vm.save();

            verify(repository).updateProgramExercise(node.getParent());
            verify(repository).updateProgramSets(any());
            verify(repository).insertProgramSets(any());

            return loaded;
        });
    }
}
