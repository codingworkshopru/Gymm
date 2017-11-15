package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collection;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.repository.ActualTrainingRepository;
import ru.codingworkshop.gymm.util.Models;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

/**
 * Created by Radik on 13.11.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ActualTrainingAdapterTest {
    @Mock private ActualTrainingRepository repository;
    @InjectMocks private ActualTrainingAdapter adapter;

    @Test
    public void getParent() throws Exception {
        adapter.getParent(11L);
        verify(repository).getActualTrainingById(11L);
    }

    @Test
    public void updateParent() throws Exception {
        ActualTraining actualTraining = Models.createActualTraining(11L, 1L);
        adapter.updateParent(actualTraining);
        verify(repository).updateActualTraining(actualTraining);
    }

    @Test
    public void insertParent() throws Exception {
        ActualTraining actualTraining = Models.createActualTraining(11L, 1L);
        adapter.insertParent(actualTraining);
        verify(repository).insertActualTraining(actualTraining);
    }

    @Test
    public void deleteParent() throws Exception {
        ActualTraining actualTraining = Models.createActualTraining(11L, 1L);
        adapter.deleteParent(actualTraining);
        verify(repository).deleteActualTraining(actualTraining);
    }

    @Test
    public void getChildren() throws Exception {
        adapter.getChildren(11L);
        verify(repository).getActualExercisesForActualTraining(11L);
    }

    @Test
    public void insertChildren() throws Exception {
        Collection<ActualExercise> exercises = Models.createActualExercises(1L);
        adapter.insertChildren(exercises);
        verify(repository).insertActualExercises(exercises);
    }

    @Test
    public void deleteChildren() throws Exception {
        Collection<ActualExercise> exercises = Models.createActualExercises(1L);
        adapter.deleteChildren(exercises);
        verify(repository).deleteActualExercises(exercises);
    }

    @Test
    public void getGrandchildren() throws Exception {
        adapter.getGrandchildren(11L);
        verify(repository).getActualSetsForActualTraining(11L);
    }
}