package ru.codingworkshop.gymm.data.tree.loader.datasource;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.repository.ActualTrainingRepository;
import ru.codingworkshop.gymm.util.Models;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 24.08.2017 as part of the Gymm project.
 */

@RunWith(MockitoJUnitRunner.class)
public class ActualTrainingDataSourceTest {
    @Mock private ActualTrainingRepository repository;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void test() throws Exception {
        final LiveData<ActualTraining> liveActualTraining = Models.createLiveActualTraining(11L, 1L);
        final LiveData<List<ActualExercise>> liveActualExercises = Models.createLiveActualExercises(12L);
        final LiveData<List<ActualSet>> liveActualSets = Models.createLiveActualSets(12L, 13L);

        when(repository.getActualTrainingById(11L)).thenReturn(liveActualTraining);
        when(repository.getActualExercisesForActualTraining(11L)).thenReturn(liveActualExercises);
        when(repository.getActualSetsForActualTraining(11L)).thenReturn(liveActualSets);

        ActualTrainingDataSource dataSource = new ActualTrainingDataSource(repository, 11L);

        assertEquals(liveActualTraining, dataSource.getParent());
        assertEquals(liveActualExercises, dataSource.getChildren());
        assertEquals(liveActualSets, dataSource.getGrandchildren());

        verify(repository).getActualTrainingById(11L);
        verify(repository).getActualExercisesForActualTraining(11L);
        verify(repository).getActualSetsForActualTraining(11L);
    }
}
