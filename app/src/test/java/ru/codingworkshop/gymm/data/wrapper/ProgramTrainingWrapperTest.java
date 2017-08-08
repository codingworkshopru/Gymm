package ru.codingworkshop.gymm.data.wrapper;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.google.common.collect.Lists;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.stream.Collectors;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.ModelsFixture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 20.06.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProgramTrainingWrapperTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ProgramTrainingRepository repository;

    @Test
    public void creation() {
        ProgramTraining training = ModelsFixture.createProgramTraining(1L, "foo");
        ProgramTrainingWrapper wrapper = new ProgramTrainingWrapper(training, repository);
        wrapper.setChildren(ModelsFixture.createProgramExercises(1));

        assertEquals("foo", wrapper.getRoot().getName());
        assertEquals(1, wrapper.getChildren().size());
    }

    @Test
    public void restoreRemoved() {
        ProgramTrainingWrapper wrapper = new ProgramTrainingWrapper(ModelsFixture.createProgramTraining(1L, "foo"), repository);
        ProgramExercise exercise = ModelsFixture.createProgramExercise(2L, 1L, 100L, false);
        wrapper.setChildren(Lists.newArrayList(exercise));
        wrapper.removeChild(exercise);
        assertFalse(wrapper.hasChildren());
        wrapper.restoreLastRemoved();
        assertTrue(wrapper.hasChildren());
    }

    @Test
    public void loadDrafting() {
        ProgramTraining training = ModelsFixture.createProgramTraining(1L, "foo");
        training.setDrafting(true);
        List<ProgramExercise> exercisesForProgram = ModelsFixture.createProgramExercises(1);

        when(repository.getDraftingProgramTraining()).thenReturn(LiveDataUtil.getLive(training));
        when(repository.getProgramExercisesForTraining(training)).thenReturn(LiveDataUtil.getLive(exercisesForProgram));

        LiveTest.verifyLiveData(
                new ProgramTrainingWrapper(repository).createTraining(),
                wrapper -> wrapper.getRoot().getId() == 1
                        && wrapper.getRoot().isDrafting()
                        && wrapper.hasChildren()
        );

        verify(repository).getProgramExercisesForTraining(training);
        verify(repository).getDraftingProgramTraining();
    }

    @Test
    public void loadNonexistentDrafting() {
        MutableLiveData<ProgramTraining> draftingTraining = new MutableLiveData<>();
        draftingTraining.setValue(null);
        when(repository.getDraftingProgramTraining()).thenReturn(draftingTraining);

        doAnswer((a) -> {
            draftingTraining.setValue(ProgramTrainingWrapper.programTrainingInstance());
            return null;
        }).when(repository).insertProgramTraining(any(ProgramTraining.class));

        LiveTest.verifyLiveData(
                new ProgramTrainingWrapper(repository).createTraining(),
                wrapper -> wrapper.getRoot().isDrafting() && !wrapper.hasChildren()
        );

        verify(repository).getDraftingProgramTraining();
        verify(repository).insertProgramTraining(any(ProgramTraining.class));
        verify(repository, never()).getProgramExercisesForTraining(null);
    }

    @Test
    public void creationUsingStaticMethod() {
        ProgramTraining training = ProgramTrainingWrapper.programTrainingInstance();

        assertTrue(training.isDrafting());
    }

    @Test
    public void load() {
        LiveData<ProgramTraining> training = ModelsFixture.createLiveProgramTraining(1L, "foo", false);
        when(repository.getProgramTrainingById(1)).thenReturn(training);
        when(repository.getProgramExercisesForTraining(1)).thenReturn(LiveDataUtil.getLive(ModelsFixture.createProgramExercises(1)));

        LiveTest.verifyLiveData(
                new ProgramTrainingWrapper(repository).load(1),
                wrapper -> wrapper.hasChildren()
                        && wrapper.getRoot().getName().equals("foo")
        );

        verify(repository).getProgramTrainingById(1);
        verify(repository).getProgramExercisesForTraining(1);
    }

    @Test
    public void saveProgramTrainingWithUnchangedExercises() {
        ProgramTraining training = ModelsFixture.createProgramTraining(1L, "foo");
        ProgramTrainingWrapper wrapper = new ProgramTrainingWrapper(training, repository);

        List<ProgramExercise> oldExercises = ModelsFixture.createProgramExercises(4);
        when(repository.getProgramExercisesForTraining(training)).thenReturn(LiveDataUtil.getLive(oldExercises));

        wrapper.setChildren(oldExercises);

        wrapper.save();

        verify(repository).getProgramExercisesForTraining(training);
        verify(repository).updateProgramTraining(training);
    }

    @Test
    public void saveProgramTrainingExercises() {
        ProgramTraining training = ModelsFixture.createProgramTraining(1L, "foo");
        ProgramTrainingWrapper wrapper = new ProgramTrainingWrapper(training, repository);

        List<ProgramExercise> oldExercises = ModelsFixture.createProgramExercises(10);

        List<ProgramExercise> newExercises = ModelsFixture.createProgramExercises(10);
        wrapper.setChildren(newExercises);

        when(repository.getProgramExercisesForTraining(training)).thenReturn(LiveDataUtil.getLive(oldExercises));

        Lists.newArrayList(0, 1, 5, 9).forEach(index -> wrapper.removeChild(newExercises.get(index)));

        wrapper.move(5, 0);
        wrapper.move(3, 5);
        wrapper.move(1, 2);

        wrapper.save();

        /* verify actions
            0 1 2 3 4 5 6 7 8 9         - source
            0 1 2 3 4 5 6 7 8           - after deletion of 9th
            0 1 2 3 4 6 7 8             - after deletion of 5th
            0 1 2 3 6 7 4 8             - after move from 3 to 5
            2 3 6 7 4 8                 - after deletion of 0th and 1st
            3 2 6 7 4 8                 - after move from 1 to 2
            8 3 2 6 7 4                 - after move from 5 to 0
         */

        verify(repository).getProgramExercisesForTraining(training);
        verify(repository).updateProgramTraining(training);
        verify(repository).deleteProgramExercises(argThat(
                toDelete -> toDelete.containsAll(Lists.newArrayList(0,1,5,9).stream().map(oldExercises::get).collect(Collectors.toList()))
        ));
        verify(repository).updateProgramExercises(argThat(
                toUpdate -> toUpdate.containsAll(Lists.newArrayList(8,3,6,7,4).stream().map(newExercises::get).collect(Collectors.toList()))
        ));
    }

}