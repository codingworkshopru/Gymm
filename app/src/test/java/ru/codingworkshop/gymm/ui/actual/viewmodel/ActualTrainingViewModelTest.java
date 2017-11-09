package ru.codingworkshop.gymm.ui.actual.viewmodel;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Objects;

import junitparams.JUnitParamsRunner;
import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.tree.node.ActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.repository.ActualTrainingRepository;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 25.08.2017 as part of the Gymm project.
 */

@RunWith(JUnitParamsRunner.class)
public class ActualTrainingViewModelTest {
    private ActualTrainingViewModel vm;

    @Mock private ProgramTrainingRepository programRepository;
    @Mock private ActualTrainingRepository actualRepository;
    @Mock private ExercisesRepository exercisesRepository;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        vm = new ActualTrainingViewModel(actualRepository, programRepository, exercisesRepository);
        when(programRepository.getProgramTrainingById(1L)).thenReturn(Models.createLiveProgramTraining(1L, "foo", false));
        when(programRepository.getProgramExercisesForTraining(1L)).thenReturn(Models.createLiveProgramExercises(1));
        when(programRepository.getProgramSetsForTraining(1L)).thenReturn(Models.createLiveProgramSets(2L, 1));
        when(exercisesRepository.getExercisesForProgramTraining(1L)).thenReturn(Models.createLiveExercises("bar"));

        when(actualRepository.insertActualTrainingWithResult(any())).thenAnswer(invocation -> {
            ActualTraining actualTraining = invocation.getArgument(0);
            actualTraining.setId(11L);
            return LiveDataUtil.getLive(11L);
        });

        when(actualRepository.insertActualSetWithResult(any())).thenAnswer(invocation -> {
            ActualSet set = invocation.getArgument(0);
            set.setId(13L);
            return LiveDataUtil.getLive(13L);
        });
    }

    @Test
    public void startTraining() throws Exception {
        LiveTest.verifyLiveData(vm.startTraining(1L), Objects::nonNull);
        ActualTrainingTree tree = vm.getActualTrainingTree();

        assertEquals(1, tree.getChildren().size());
        assertEquals(11L, tree.getParent().getId());
        verify(actualRepository).insertActualTrainingWithResult(any());

        verifyProgramLoaded();

        // don't start if already started
        LiveTest.verifyLiveData(vm.startTraining(0L), Objects::nonNull);
        assertEquals(tree, vm.getActualTrainingTree());
    }

    @Test
    public void loadTraining() throws Exception {
        stubActualRepository();

        LiveTest.verifyLiveData(vm.loadTraining(11L), Objects::nonNull);

        ActualTrainingTree tree = vm.getActualTrainingTree();
        assertEquals(11L, tree.getParent().getId());
        assertEquals(12L, tree.getChildren().get(0).getParent().getId());
        assertEquals(13L, tree.getChildren().get(0).getChildren().get(0).getId());

        verify(actualRepository, times(2)).getActualTrainingById(11L); // FIXME: 26.08.2017 should be times(1)
        verify(actualRepository).getActualExercisesForActualTraining(11L);
        verify(actualRepository).getActualSetsForActualTraining(11);

        verifyProgramLoaded();

        LiveTest.verifyLiveData(vm.loadTraining(0L), Objects::nonNull);
        assertEquals(tree, vm.getActualTrainingTree());
    }

    private void stubActualRepository() {
        when(actualRepository.getActualTrainingById(11L)).thenReturn(Models.createLiveActualTraining(11L, 1L));
        when(actualRepository.getActualExercisesForActualTraining(11L)).thenReturn(Models.createLiveActualExercises(12L));
        when(actualRepository.getActualSetsForActualTraining(11L)).thenReturn(Models.createLiveActualSets(12L, 13L));
    }

    @Test
    public void finishTrainingWithoutExercises() throws Exception {
        stubActualRepository();
        LiveTest.verifyLiveData(vm.loadTraining(11L), Objects::nonNull);
        vm.getActualTrainingTree().getChildren().clear();

        vm.finishTraining();

        verify(actualRepository).deleteActualTraining(argThat(at -> at.getId() == 11L));
    }

    @Test
    public void finishTrainingWithoutSets() throws Exception {
        stubActualRepository();
        LiveTest.verifyLiveData(vm.loadTraining(11L), Objects::nonNull);
        vm.getActualTrainingTree().getChildren().get(0).getChildren().clear();

        vm.finishTraining();

        verify(actualRepository).deleteActualTraining(argThat(at -> at.getId() == 11L));

    }

    @Test
    public void finishTraining() throws Exception {
        stubActualRepository();
        LiveTest.verifyLiveData(vm.loadTraining(11L), Objects::nonNull);
        final ActualExercise foo = Models.createActualExercise(20L, "foo", 11L, 4L);
        vm.getActualTrainingTree().addChild(new ActualExerciseNode(foo));

        vm.finishTraining();

        verify(actualRepository).deleteActualExercises(argThat(exs -> exs.iterator().next() == foo));
        verify(actualRepository).updateActualTraining(argThat(t -> t.getFinishTime() != null));
    }

    @Test
    public void createActualExercise() throws Exception {
        doAnswer(invocation -> {
            ActualExercise exercise = invocation.getArgument(0);
            exercise.setId(12L);
            return null;
        }).when(actualRepository).insertActualExercise(any(ActualExercise.class));

        final LiveData<ActualTrainingTree> liveLoaded = vm.startTraining(1L);
        LiveTest.verifyLiveData(liveLoaded, l -> {
            vm.createActualExercise(0);
            assertEquals(12L, l.getChildren().get(0).getParent().getId());

            // check if actual exercise creates for the same index (it mustn't)
            vm.createActualExercise(0);

            return true;
        });

        verify(actualRepository).insertActualExercise(any());
    }

    @Test
    public void createActualSet() throws Exception {
        LiveTest.verifyLiveData(vm.startTraining(1L), Objects::nonNull);

        vm.createActualExercise(0);
        final ActualSet toInsert = Models.createActualSet(0L, 0L, 10);
        toInsert.setWeight(5.5);
        LiveData<Long> liveLoaded = vm.createActualSet(0, toInsert);

        LiveTest.verifyLiveData(liveLoaded, id -> id == 13L);

        final ActualSet actualSet = vm.getActualTrainingTree().getChildren().get(0).getChildren().get(0);
        assertEquals(13L, actualSet.getId());
        assertEquals(10, actualSet.getReps());
        assertEquals(5.5, actualSet.getWeight());

        verify(actualRepository).insertActualSetWithResult(any());
    }

    @Test
    public void updateActualSet() throws Exception {
        stubActualRepository();

        LiveTest.verifyLiveData(vm.loadTraining(11L), loaded -> {
            final ActualSet actualSet = Models.createActualSet(13L, 12L, 3);
            actualSet.setWeight(5.5);
            vm.updateActualSet(0, actualSet);
            verify(actualRepository).updateActualSet(argThat(s -> s == actualSet && s.getReps() == 3 && s.getId() == 13L && s.getWeight() == 5.5));
            return true;
        });
    }

    private void verifyProgramLoaded() throws Exception {
        verify(programRepository).getProgramTrainingById(1L);
        verify(programRepository).getProgramExercisesForTraining(1L);
        verify(programRepository).getProgramSetsForTraining(1L);
        verify(exercisesRepository).getExercisesForProgramTraining(1L);
    }
}
