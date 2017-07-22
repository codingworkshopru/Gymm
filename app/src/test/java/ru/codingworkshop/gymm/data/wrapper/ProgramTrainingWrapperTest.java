package ru.codingworkshop.gymm.data.wrapper;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.stream.Collectors;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.entity.common.Sortable;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.LiveTest;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by Радик on 20.06.2017.
 */

@RunWith(JUnit4.class)
public class ProgramTrainingWrapperTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private ProgramTrainingRepository repository;

    @Before
    public void init() {
        repository = mock(ProgramTrainingRepository.class);
    }

    @Test
    public void creation() {
        ProgramTraining training = createTraining(1L, "foo");
        ProgramTrainingWrapper wrapper = new ProgramTrainingWrapper(training);
        wrapper.setProgramExercises(createExercises(1));

        assertEquals("foo", wrapper.getProgramTraining().getName());
        assertEquals(1, wrapper.getProgramExercises().size());
    }

    @Test
    public void addAndRemove() {
        ProgramTrainingWrapper wrapper = new ProgramTrainingWrapper(new ProgramTraining());
        ProgramExercise programExercise = new ProgramExercise();
        wrapper.addProgramExercise(programExercise);

        assertTrue(wrapper.hasProgramExercises());

        wrapper.removeProgramExercise(programExercise);

        assertFalse(wrapper.hasProgramExercises());
    }

    @Test
    public void lastRemoved() {
        ProgramTraining training = createTraining(1L, "foo");
        ProgramTrainingWrapper wrapper = new ProgramTrainingWrapper(training);
        List<ProgramExercise> exercises = createExercises(4);
        wrapper.setProgramExercises(exercises);
        wrapper.removeProgramExercise(exercises.get(0));

        wrapper.restoreLastRemoved();
        checkOrder(wrapper.getProgramExercises());
        assertTrue(exercises.equals(wrapper.getProgramExercises()));
    }

    @Test
    public void sortableTest() {
        ProgramTrainingWrapper wrapper = new ProgramTrainingWrapper(new ProgramTraining());
        checkOrder(wrapper.getProgramExercises());
        List<ProgramExercise> exercises = createExercises(3);

        exercises.forEach(wrapper::addProgramExercise);
        checkOrder(wrapper.getProgramExercises());

        exercises.forEach(exercise -> removeAndCheck(wrapper, exercise));
        assertFalse(wrapper.hasProgramExercises());

        Lists.reverse(exercises).forEach(wrapper::addProgramExercise);

        wrapper.moveProgramExercise(2, 1);
        checkOrder(wrapper.getProgramExercises());
        wrapper.moveProgramExercise(0, 2);
        checkOrder(wrapper.getProgramExercises());

        for (int i = 0; i < exercises.size(); i++)
            assertEquals(exercises.get(i), wrapper.getProgramExercises().get(i));
    }

    private void removeAndCheck(ProgramTrainingWrapper wrapper, ProgramExercise entity) {
        wrapper.removeProgramExercise(entity);
        checkOrder(wrapper.getProgramExercises());
    }

    private void checkOrder(List<? extends Sortable> sorted) {
        if (sorted.isEmpty()) return;

        for (int i = 0; i < sorted.size(); i++)
            if (sorted.get(i).getSortOrder() != i)
                fail("sort orders incorrect");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testImmutability() {
        ProgramTrainingWrapper wrapper = new ProgramTrainingWrapper(createTraining(1L, "foo"));
        List<ProgramExercise> exercises = wrapper.getProgramExercises();
        exercises.add(new ProgramExercise());
    }

    @Test
    public void loadDrafting() {
        ProgramTraining training = createTraining(1L, "foo");
        training.setDrafting(true);
        List<ProgramExercise> exercisesForProgram = createExercises(1);

        when(repository.getDraftingProgramTraining()).thenReturn(LiveDataUtil.getLive(training));
        when(repository.getProgramExercisesForTraining(training)).thenReturn(LiveDataUtil.getLive(exercisesForProgram));

        LiveTest.verifyLiveData(
                ProgramTrainingWrapper.createTraining(repository),
                wrapper -> wrapper.getProgramTraining().getId() == 1
                        && wrapper.getProgramTraining().isDrafting()
                        && wrapper.hasProgramExercises()
        );

        verify(repository).getProgramExercisesForTraining(training);
        verify(repository).getDraftingProgramTraining();
    }

    @Test
    public void loadNonexistentDrafting() {
        MutableLiveData<ProgramTraining> draftingTraining = new MutableLiveData<>();
        draftingTraining.setValue(null);
        when(repository.getDraftingProgramTraining()).thenReturn(draftingTraining);

        when(repository.getProgramExercisesForTraining(null)).thenReturn(LiveDataUtil.getLive(Lists.newArrayList()));

        ProgramTraining drafting = new ProgramTraining();
        drafting.setDrafting(true);
        doAnswer((a) -> {
            draftingTraining.setValue(drafting);
            return null;
        }).when(repository).insertProgramTraining(any(ProgramTraining.class));

        LiveTest.verifyLiveData(
                ProgramTrainingWrapper.createTraining(repository),
                wrapper -> wrapper.getProgramTraining().isDrafting() && !wrapper.hasProgramExercises()
        );

        verify(repository).getDraftingProgramTraining();
        verify(repository, never()).getProgramExercisesForTraining(null);
    }

    @Test
    public void creationUsingStaticMethod() {
        ProgramTraining training = ProgramTrainingWrapper.createTraining();

        assertTrue(training.isDrafting());
    }

    @Test
    public void loadUsingStaticMethod() {
        ProgramTraining training = createTraining(1L, "foo");
        when(repository.getProgramTrainingById(1)).thenReturn(LiveDataUtil.getLive(training));
        when(repository.getProgramExercisesForTraining(1)).thenReturn(LiveDataUtil.getLive(createExercises(1)));

        LiveTest.verifyLiveData(
                ProgramTrainingWrapper.load(1, repository),
                wrapper -> wrapper.hasProgramExercises()
                        && wrapper.getProgramTraining().getName().equals("foo")
        );

        verify(repository).getProgramTrainingById(1);
        verify(repository).getProgramExercisesForTraining(1);
    }

    @Test
    public void saveProgramTrainingWithUnchangedExercises() {
        ProgramTraining training = createTraining(1L, "foo");
        training.setName("bar");
        ProgramTrainingWrapper wrapper = new ProgramTrainingWrapper(training);

        List<ProgramExercise> oldExercises = createExercises(4);
        when(repository.getProgramExercisesForTraining(training)).thenReturn(LiveDataUtil.getLive(oldExercises));

        wrapper.setProgramExercises(Lists.newArrayList(oldExercises));

        wrapper.save(repository);

        verify(repository).getProgramExercisesForTraining(training);
        verify(repository).updateProgramTraining(training);
    }

    @Test
    public void saveProgramTrainingExercises() {
        ProgramTraining training = createTraining(1L, "foo");
        ProgramTrainingWrapper wrapper = new ProgramTrainingWrapper(training);

        List<ProgramExercise> oldExercises = createExercises(10);

        List<ProgramExercise> newExercises = createExercises(10);
        newExercises.forEach(wrapper::addProgramExercise);

        when(repository.getProgramExercisesForTraining(training)).thenReturn(LiveDataUtil.getLive(oldExercises));

        Lists.newArrayList(0, 1, 5, 9).forEach(index -> wrapper.removeProgramExercise(newExercises.get(index)));

        wrapper.moveProgramExercise(5, 0);
        wrapper.moveProgramExercise(3, 5);
        wrapper.moveProgramExercise(1, 2);

        wrapper.save(repository);

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

    private static ProgramTraining createTraining(long id, String name) {
        ProgramTraining result = new ProgramTraining();
        result.setId(id);
        result.setName(name);
        return result;
    }

    private static List<ProgramExercise> createExercises(int count) {
        List<ProgramExercise> exercises = Lists.newArrayListWithCapacity(count);
        for (int i = 0; i < count; i++) {
            ProgramExercise exercise = new ProgramExercise();
            exercise.setId(2 + i);
            exercise.setSortOrder(i);
            exercises.add(exercise);
        }
        return exercises;
    }
}