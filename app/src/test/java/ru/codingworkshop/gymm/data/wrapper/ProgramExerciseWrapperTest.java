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

    @Before
    public void init() {
        Exercise exercise = new Exercise();
        exercise.setId(100L);
        exercise.setName("foobar");

        exercisesRepository = mock(ExercisesRepository.class);
        when(exercisesRepository.getExerciseById(100L)).thenReturn(LiveDataUtil.getLive(exercise));
    }

    @Test
    public void settingWrappedValues() {
        ProgramExerciseWrapper wrapper = new ProgramExerciseWrapper();
        wrapper.setProgramExercise(createExercise());
        wrapper.setProgramSets(createSets(10));
        wrapper.setExercise(new Exercise());

        assertNotNull(wrapper.getExercise());
        assertNotNull(wrapper.getProgramExercise());
        assertEquals(10, wrapper.getProgramSets().size());
    }

    @Test
    public void creationProgramExercise() {
        ProgramExercise exercise = ProgramExerciseWrapper.createProgramExercise(1L);
        assertEquals(0, exercise.getExerciseId());
        assertTrue(exercise.isDrafting());
        assertEquals(1L, exercise.getProgramTrainingId());
    }

    @Test
    public void childrenTest() {
        ProgramExerciseWrapper wrapper = new ProgramExerciseWrapper();
        assertFalse(wrapper.hasProgramSets());

        // add
        ProgramSet set = createSets(1).get(0);
        wrapper.addProgramSet(set);
        assertEquals(set, wrapper.getProgramSets().get(0));

        // remove
        wrapper.removeProgramSet(set);
        assertFalse(wrapper.hasProgramSets());
        wrapper.addProgramSet(set);
        assertTrue(wrapper.hasProgramSets());
        wrapper.removeProgramSet(0);
        assertFalse(wrapper.hasProgramSets());

        // move
        List<ProgramSet> sets = createSets(10);
        wrapper.setProgramSets(sets);
        wrapper.move(3, 7);
        assertEquals(sets.get(3), wrapper.getProgramSets().get(7));

        // restore
        ProgramSet toRemove = wrapper.getProgramSets().get(8);
        wrapper.removeProgramSet(8);
        assertEquals(9, wrapper.getProgramSets().size());
        wrapper.restoreLastRemoved();
        assertEquals(toRemove, wrapper.getProgramSets().get(8));
    }

    @Test
    public void creation() {
        MutableLiveData<ProgramExercise> liveProgramExercise = new MutableLiveData<>();
        liveProgramExercise.setValue(null);

        ProgramTrainingRepository repository = mock(ProgramTrainingRepository.class);
        when(repository.getDraftingProgramExercise(1L)).thenReturn(liveProgramExercise);

        when(exercisesRepository.getExerciseById(0L)).thenReturn(LiveDataUtil.getLive(null));

        doAnswer(invocation -> {
            liveProgramExercise.setValue(ProgramExerciseWrapper.createProgramExercise(1L));
            return null;
        }).when(repository).insertProgramExercise(any(ProgramExercise.class));

        LiveData<ProgramExerciseWrapper> wrapper = ProgramExerciseWrapper.createProgramExercise(repository, exercisesRepository, 1L);
        LiveTest.verifyLiveData(
                wrapper,
                w -> w.getProgramExercise().isDrafting()
                        && w.getProgramExercise().getProgramTrainingId() == 1L
                        && w.getExercise() == null
        );

        verify(repository).getDraftingProgramExercise(1L);
        verify(repository).insertProgramExercise(any(ProgramExercise.class));
        verify(exercisesRepository).getExerciseById(0L);
        verify(repository).getProgramSetsForExercise(liveProgramExercise.getValue());
    }

    @Test
    public void loadDrafting() {
        LiveData<ProgramExercise> draftingExercise = LiveDataUtil.getLive(createExercise());
        draftingExercise.getValue().setDrafting(true);
        LiveData<List<ProgramSet>> sets = LiveDataUtil.getLive(createSets(10));

        ProgramTrainingRepository repository = mock(ProgramTrainingRepository.class);
        when(repository.getDraftingProgramExercise(1L)).thenReturn(draftingExercise);
        when(repository.getProgramSetsForExercise(draftingExercise.getValue())).thenReturn(sets);

        LiveData<ProgramExerciseWrapper> liveWrapper = ProgramExerciseWrapper.createProgramExercise(repository, exercisesRepository, 1L);

        LiveTest.verifyLiveData(
                liveWrapper,
                wrapper -> wrapper.getProgramExercise().getProgramTrainingId() == 1L
                        && wrapper.getProgramExercise().getId() == 2L
                        && wrapper.getProgramExercise().isDrafting()
                        && wrapper.getProgramSets().size() == 10
                        && wrapper.getExercise().getId() == 100L
        );

        verify(exercisesRepository).getExerciseById(100L);
        verify(repository).getDraftingProgramExercise(1L);
        verify(repository).getProgramSetsForExercise(draftingExercise.getValue());
        verify(repository, never()).insertProgramExercise(any());
    }

    @Test
    public void load() {
        LiveData<ProgramExercise> loadedExercise = LiveDataUtil.getLive(createExercise());
        LiveData<List<ProgramSet>> sets = LiveDataUtil.getLive(createSets(10));

        ProgramTrainingRepository programRepository = mock(ProgramTrainingRepository.class);
        when(programRepository.getProgramExerciseById(2L)).thenReturn(loadedExercise);
        when(programRepository.getProgramSetsForExercise(2L)).thenReturn(sets);

        LiveData<ProgramExerciseWrapper> liveWrapper = ProgramExerciseWrapper.load(programRepository, exercisesRepository, 2L);

        LiveTest.verifyLiveData(
                liveWrapper,
                wrapper -> wrapper.getProgramExercise().getProgramTrainingId() == 1L
                        && wrapper.getProgramExercise().getId() == 2L
                        && !wrapper.getProgramExercise().isDrafting()
                        && wrapper.getProgramSets().size() == 10
                        && wrapper.getExercise().getId() == 100L
        );

        verify(exercisesRepository).getExerciseById(100L);
        verify(programRepository).getProgramExerciseById(2L);
        verify(programRepository).getProgramSetsForExercise(2L);
    }

    @Test
    public void saveAll() {
        ProgramExerciseWrapper wrapper = new ProgramExerciseWrapper();

        ProgramExercise programExercise = createExercise();
        wrapper.setProgramExercise(programExercise);

        List<ProgramSet> oldSets = createSets(10);
        List<ProgramSet> newSets = Lists.newArrayList(oldSets);
        newSets.removeIf(set -> set.getId() % 2 == 0);
        ProgramSet newSet = new ProgramSet();
        newSet.setId(51L);
        newSets.add(newSet);
        wrapper.setProgramSets(newSets);

        wrapper.move(2, 4);

        ProgramTrainingRepository repository = mock(ProgramTrainingRepository.class);
        when(repository.getProgramSetsForExercise(programExercise)).thenReturn(LiveDataUtil.getLive(oldSets));
        wrapper.save(repository);

        verify(repository).getProgramSetsForExercise(programExercise);
        verify(repository).updateProgramExercise(programExercise);
        verify(repository).insertProgramSets(argThat(sets -> sets.size() == 1 && sets.stream().findFirst().get() == newSet));
        verify(repository).deleteProgramSets(argThat(sets -> sets.stream().allMatch(set -> set.getId() % 2 == 0)));
        verify(repository).updateProgramSets(argThat(sets -> sets.stream().allMatch(set -> set.getId() >= 2 && set.getId() <= 4)));
    }

    private ProgramExercise createExercise() {
        ProgramExercise exercise = new ProgramExercise();
        exercise.setProgramTrainingId(1L);
        exercise.setId(2L);
        exercise.setExerciseId(100L);
        return exercise;
    }

    private List<ProgramSet> createSets(int count) {
        List<ProgramSet> result = Lists.newArrayList();
        for (int i = 0; i < count; i++) {
            ProgramSet set = new ProgramSet();
            set.setId(i + 1);
            set.setSortOrder(i);
            result.add(set);
        }

        return result;
    }
}
