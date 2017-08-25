package ru.codingworkshop.gymm.data.wrapper;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.Models;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 26.06.2017.
 */

@RunWith(JUnit4.class)
public class ProgramExerciseWrapperTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private ExercisesRepository exercisesRepository;
    private ProgramTrainingRepository repository;
    private ProgramExerciseWrapper wrapper;

    @Before
    public void init() {
        Exercise exercise = Models.createExercise(100L, "foobar");
        repository = mock(ProgramTrainingRepository.class);

        exercisesRepository = mock(ExercisesRepository.class);
        when(exercisesRepository.getExerciseById(100L)).thenReturn(LiveDataUtil.getLive(exercise));

        wrapper = new ProgramExerciseWrapper(repository, exercisesRepository);
    }

    @Test
    public void settingWrappedValues() {
        wrapper.setRoot(Models.createProgramExercise(2L, 1L, 33L, false));
        wrapper.setChildren(Models.createProgramSets(10));
        wrapper.setExercise(Models.createExercise(1, "foo"));

        assertNotNull(wrapper.getExercise());
        assertNotNull(wrapper.getRoot());
        assertEquals(10, wrapper.getChildren().size());
    }

    @Test
    public void creationProgramExercise() {
        ProgramExercise exercise = ProgramExerciseWrapper.programExerciseInstance(1L);
        assertEquals(0, exercise.getExerciseId());
        assertTrue(exercise.isDrafting());
        assertEquals(1L, exercise.getProgramTrainingId());
    }

    @Test
    public void childrenTest() {
        assertFalse(wrapper.hasChildren());

        // add
        ProgramSet set = Models.createProgramSets(1).get(0);
        wrapper.addChild(set);
        assertEquals(set, wrapper.getChildren().get(0));

        // remove
        wrapper.removeChild(set);
        assertFalse(wrapper.hasChildren());
        wrapper.addChild(set);
        assertTrue(wrapper.hasChildren());
        wrapper.removeChild(0);
        assertFalse(wrapper.hasChildren());

        // move
        List<ProgramSet> sets = Models.createProgramSets(10);
        wrapper.setChildren(sets);
        wrapper.move(3, 7);
        assertEquals(sets.get(3), wrapper.getChildren().get(7));

        // restore
        ProgramSet toRemove = wrapper.getChildren().get(8);
        wrapper.removeChild(8);
        assertEquals(9, wrapper.getChildren().size());
        wrapper.restoreLastRemoved();
        assertEquals(toRemove, wrapper.getChildren().get(8));
    }

    @Test
    public void creation() {
        MutableLiveData<ProgramExercise> liveProgramExercise = new MutableLiveData<>();
        liveProgramExercise.setValue(null);

        when(repository.getDraftingProgramExercise(1L)).thenReturn(liveProgramExercise);

        when(exercisesRepository.getExerciseById(0L)).thenReturn(LiveDataUtil.getLive(null));

        doAnswer(invocation -> {
            liveProgramExercise.setValue(ProgramExerciseWrapper.programExerciseInstance(1L));
            return null;
        }).when(repository).insertProgramExercise(any(ProgramExercise.class));

        LiveData<ProgramExerciseWrapper> liveWrapper = wrapper.createProgramExercise(1L);
        LiveTest.verifyLiveData(
                liveWrapper,
                w -> w.getRoot().isDrafting()
                        && w.getRoot().getProgramTrainingId() == 1L
                        && w.getExercise() == null
        );

        verify(repository).getDraftingProgramExercise(1L);
        verify(repository).insertProgramExercise(any(ProgramExercise.class));
        verify(exercisesRepository).getExerciseById(0L);
        verify(repository).getProgramSetsForExercise(liveProgramExercise.getValue());
    }

    @Test
    public void loadDrafting() {
        LiveData<ProgramExercise> draftingExercise = LiveDataUtil.getLive(Models.createProgramExercise(2L, 1L, 100L, false));
        draftingExercise.getValue().setDrafting(true);
        LiveData<List<ProgramSet>> sets = LiveDataUtil.getLive(Models.createProgramSets(10));

        when(repository.getDraftingProgramExercise(1L)).thenReturn(draftingExercise);
        when(repository.getProgramSetsForExercise(draftingExercise.getValue())).thenReturn(sets);

        LiveData<ProgramExerciseWrapper> liveWrapper = wrapper.createProgramExercise(1L);

        LiveTest.verifyLiveData(
                liveWrapper,
                w -> w.getRoot().getProgramTrainingId() == 1L
                        && w.getRoot().getId() == 2L
                        && w.getRoot().isDrafting()
                        && w.getChildren().size() == 10
                        && w.getExercise().getId() == 100L
        );

        verify(exercisesRepository).getExerciseById(100L);
        verify(repository).getDraftingProgramExercise(1L);
        verify(repository).getProgramSetsForExercise(draftingExercise.getValue());
        verify(repository, never()).insertProgramExercise(any());
    }

    @Test
    public void load() {
        LiveData<ProgramExercise> loadedExercise = LiveDataUtil.getLive(Models.createProgramExercise(2L, 1L, 100L, false));
        LiveData<List<ProgramSet>> sets = Models.createLiveProgramSets(10);

        when(repository.getProgramExerciseById(2L)).thenReturn(loadedExercise);
        when(repository.getProgramSetsForExercise(2L)).thenReturn(sets);

        LiveData<ProgramExerciseWrapper> liveWrapper = wrapper.load(2L);

        LiveTest.verifyLiveData(
                liveWrapper,
                wrapper -> wrapper.getRoot().getProgramTrainingId() == 1L
                        && wrapper.getRoot().getId() == 2L
                        && !wrapper.getRoot().isDrafting()
                        && wrapper.getChildren().size() == 10
                        && wrapper.getExercise().getId() == 100L
        );

        verify(exercisesRepository).getExerciseById(100L);
        verify(repository).getProgramExerciseById(2L);
        verify(repository).getProgramSetsForExercise(2L);
    }

    @Test
    public void saveAll() {
        ProgramExercise programExercise = Models.createProgramExercise(2L, 1L, 33L, false);
        wrapper.setRoot(programExercise);

        List<ProgramSet> oldSets = Models.createProgramSets(10);
        List<ProgramSet> newSets = Lists.newArrayList(oldSets);
        newSets.removeIf(set -> set.getId() % 2 == 0);
        ProgramSet newSet = Models.createProgramSet(51L, 2L, 3);
        newSets.add(newSet);
        wrapper.setChildren(newSets);

        wrapper.move(2, 4);

        when(repository.getProgramSetsForExercise(programExercise)).thenReturn(LiveDataUtil.getLive(oldSets));
        wrapper.save();

        verify(repository).getProgramSetsForExercise(programExercise);
        verify(repository).updateProgramExercise(programExercise);
        verify(repository).insertProgramSets(argThat(sets -> sets.size() == 1 && sets.stream().findFirst().get() == newSet));
        verify(repository).deleteProgramSets(argThat(sets -> sets.stream().allMatch(set -> set.getId() % 2 == 0)));
        verify(repository).updateProgramSets(argThat(sets -> sets.stream().allMatch(set -> set.getId() >= 2 && set.getId() <= 4)));
    }
}
