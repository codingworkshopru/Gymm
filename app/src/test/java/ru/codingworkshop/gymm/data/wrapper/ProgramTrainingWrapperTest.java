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

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.LiveTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
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
        ProgramTraining training = createTraining(1L, "foo");
        ProgramTrainingWrapper wrapper = new ProgramTrainingWrapper(training);
        wrapper.setProgramExercises(createExercises(1));

        assertEquals("foo", wrapper.getProgramTraining().getName());
        assertEquals(1, wrapper.getProgramExercises().size());
    }

    @Test
    public void restoreRemoved() {
        ProgramTrainingWrapper wrapper = new ProgramTrainingWrapper(createTraining(1L, "foo"));
        ProgramExercise exercise = createExercises(1).get(0);
        wrapper.addProgramExercise(exercise);
        wrapper.removeProgramExercise(exercise);
        assertFalse(wrapper.hasProgramExercises());
        wrapper.restoreLastRemoved();
        assertTrue(wrapper.hasProgramExercises());
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

        doAnswer((a) -> {
            draftingTraining.setValue(ProgramTrainingWrapper.createTraining());
            return null;
        }).when(repository).insertProgramTraining(any(ProgramTraining.class));

        LiveTest.verifyLiveData(
                ProgramTrainingWrapper.createTraining(repository),
                wrapper -> wrapper.getProgramTraining().isDrafting() && !wrapper.hasProgramExercises()
        );

        verify(repository).getDraftingProgramTraining();
        verify(repository).insertProgramTraining(any(ProgramTraining.class));
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
    public void loadWithWrappedProgramExercises() {
        ProgramTraining training = createTraining(1L, "foo");
        List<ProgramExercise> programExercises = createExercises(10);
        List<Exercise> exercises = Lists.newArrayList(100L, 101L).stream().map(id -> {Exercise e = new Exercise(); e.setId(id); e.setName(id%10==0?"bar":"baz");return e;}).collect(Collectors.toList());
        programExercises.forEach(ex -> ex.setExerciseId(exercises.get(ex.getId()%2==0?0:1).getId()));
        List<ProgramSet> sets = Lists.newArrayList();
        programExercises.forEach(programExercise -> {
            for (int i = 0; i < programExercise.getId() - 1; i++) {
                ProgramSet set = new ProgramSet();
                set.setId(programExercise.getId() * 10 + i);
                set.setSortOrder(i);
                set.setProgramExerciseId(programExercise.getId());
                sets.add(set);
            }
        });

        ExercisesRepository exercisesRepository = mock(ExercisesRepository.class);
        when(exercisesRepository.getExercisesForProgramTraining(1L)).thenReturn(LiveDataUtil.getLive(exercises));

        when(repository.getProgramTrainingById(1L)).thenReturn(LiveDataUtil.getLive(training));
        when(repository.getProgramExercisesForTraining(1L)).thenReturn(LiveDataUtil.getLive(programExercises));
        when(repository.getProgramSetsForTraining(1L)).thenReturn(LiveDataUtil.getLive(sets));

        LiveData<ProgramTrainingWrapper> liveWrapper = ProgramTrainingWrapper.loadWithWrappedProgramExercises(1L, repository, exercisesRepository);
        LiveTest.verifyLiveData(
                liveWrapper,
                wrapper -> {
                    assertEquals(training, wrapper.getProgramTraining());
                    assertEquals(programExercises, wrapper.getProgramExercises());
                    assertEquals(10, wrapper.getExerciseWrappers().size());
                    assertEquals(sets, wrapper.getExerciseWrappers().stream().flatMap(w -> w.getProgramSets().stream()).collect(Collectors.toList()));
                    assertEquals(exercises, wrapper.getExerciseWrappers().stream().map(ProgramExerciseWrapper::getExercise).distinct().sorted((a,b) -> (int)(a.getId()-b.getId())).collect(Collectors.toList()));
                    
                    return true;
                }
        );

        verify(repository).getProgramTrainingById(1L);
        verify(repository).getProgramExercisesForTraining(1L);
        verify(repository).getProgramSetsForTraining(1L);
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
            exercise.setProgramTrainingId(1L);
            exercise.setSortOrder(i);
            exercises.add(exercise);
        }
        return exercises;
    }
}