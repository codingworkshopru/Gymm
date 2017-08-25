package ru.codingworkshop.gymm.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.db.dao.MuscleGroupDao;
import ru.codingworkshop.gymm.util.Models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 24.08.2017 as part of the Gymm project.
 */

@RunWith(MockitoJUnitRunner.class)
public class MuscleGroupsRepositoryTest {
    @Mock private MuscleGroupDao dao;
    private MuscleGroupsRepository repository;

    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        repository = new MuscleGroupsRepository(dao);
    }

    @Test
    public void isEmpty() throws Exception {
        when(dao.getMuscleGroupsCount()).thenReturn(1);
        assertFalse(repository.isEmpty());

        when(dao.getMuscleGroupsCount()).thenReturn(0);
        assertTrue(repository.isEmpty());

        verify(dao, times(2)).getMuscleGroupsCount();
    }

    @Test
    public void getMuscleGroups() throws Exception {
        final LiveData<List<MuscleGroup>> liveMuscleGroups = Models.createLiveMuscleGroups(1L);
        when(dao.getAllMuscleGroups()).thenReturn(liveMuscleGroups);
        assertEquals(liveMuscleGroups, repository.getMuscleGroups());
        verify(dao).getAllMuscleGroups();
    }

    @Test
    public void insertMuscleGroups() throws Exception {
        final List<MuscleGroup> muscleGroups = Models.createMuscleGroups(1L);
        repository.insertMuscleGroups(muscleGroups);
        verify(dao).insertMuscleGroups(muscleGroups);
    }

    @Test
    public void getMuscleGroupById() throws Exception {
        final LiveData<MuscleGroup> muscleGroup = Models.createLiveMuscleGroup(1L, "foo");
        when(dao.getMuscleGroupById(1L)).thenReturn(muscleGroup);
        assertEquals(muscleGroup, repository.getMuscleGroupById(1L));
        verify(dao).getMuscleGroupById(1L);
    }

    @Test
    public void getSecondaryMuscleGroupsForExercise() throws Exception {
        final LiveData<List<MuscleGroup>> liveMuscleGroups = Models.createLiveMuscleGroups(2L);
        when(dao.getSecondaryMuscleGroupsForExercise(1L)).thenReturn(liveMuscleGroups);
        assertEquals(liveMuscleGroups, repository.getSecondaryMuscleGroupsForExercise(1L));
        verify(dao).getSecondaryMuscleGroupsForExercise(1L);
    }
}