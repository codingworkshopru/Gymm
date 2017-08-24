package ru.codingworkshop.gymm.data.tree.loader.datasource;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.LiveTest;
import ru.codingworkshop.gymm.util.ModelsFixture;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 24.08.2017 as part of the Gymm project.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProgramExerciseDataSourceTest {
    @Mock private ProgramTrainingRepository repository;
    @Mock private ExercisesRepository exercisesRepository;

    @Rule public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Test
    public void test() throws Exception {
        final LiveData<ProgramExercise> liveProgramExercise = ModelsFixture.createLiveProgramExercise(2L, 1L, false);
        final LiveData<List<ProgramSet>> liveProgramSets = ModelsFixture.createLiveProgramSets(1);
        final LiveData<Exercise> liveExercise = ModelsFixture.createLiveExercise(100L, "foo");
        when(repository.getProgramExerciseById(2L)).thenReturn(liveProgramExercise);
        when(repository.getProgramSetsForExercise(2L)).thenReturn(liveProgramSets);
        when(exercisesRepository.getExerciseById(100L)).thenReturn(liveExercise);

        ProgramExerciseDataSource dataSource = new ProgramExerciseDataSource(repository, exercisesRepository, 2L);
        assertEquals(liveProgramExercise, dataSource.getParent());
        assertEquals(liveProgramSets, dataSource.getChildren());
        LiveTest.verifyLiveData(dataSource.getExercise(), e -> e.getName().equals("foo"));

        verify(repository).getProgramExerciseById(2L);
        verify(repository).getProgramSetsForExercise(2L);
        verify(exercisesRepository).getExerciseById(100L);
    }
}