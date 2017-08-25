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
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.Models;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 24.08.2017 as part of the Gymm project.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProgramTrainingDataSourceTest {
    @Mock private ProgramTrainingRepository repository;
    @Mock private ExercisesRepository exercisesRepository;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void test() throws Exception {
        final LiveData<ProgramTraining> liveProgramTraining = Models.createLiveProgramTraining(1L, "foo", false);
        final LiveData<List<ProgramExercise>> liveProgramExercises = Models.createLiveProgramExercises(1);
        final LiveData<List<ProgramSet>> liveProgramSets = Models.createLiveProgramSets(1);
        final LiveData<List<Exercise>> liveExercises = Models.createLiveExercises("foobar", "baz");

        when(repository.getProgramTrainingById(1L)).thenReturn(liveProgramTraining);
        when(repository.getProgramExercisesForTraining(1L)).thenReturn(liveProgramExercises);
        when(repository.getProgramSetsForTraining(1L)).thenReturn(liveProgramSets);
        when(exercisesRepository.getExercisesForProgramTraining(1L)).thenReturn(liveExercises);

        ProgramTrainingDataSource dataSource = new ProgramTrainingDataSource(repository, exercisesRepository, 1L);

        assertEquals(liveProgramTraining, dataSource.getParent());
        assertEquals(liveProgramExercises, dataSource.getChildren());
        assertEquals(liveProgramSets, dataSource.getGrandchildren());
        assertEquals(liveExercises, dataSource.getExercises());
    }
}
