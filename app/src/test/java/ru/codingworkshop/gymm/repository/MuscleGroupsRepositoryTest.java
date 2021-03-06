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

import io.reactivex.Flowable;
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
    public void isEmpty() {
        when(dao.getMuscleGroupsCount()).thenReturn(1);
        assertFalse(repository.isEmpty());

        when(dao.getMuscleGroupsCount()).thenReturn(0);
        assertTrue(repository.isEmpty());

        verify(dao, times(2)).getMuscleGroupsCount();
    }

    @Test
    public void getMuscleGroups() {
        final LiveData<List<MuscleGroup>> liveMuscleGroups = Models.createLiveMuscleGroups(1L);
        when(dao.getAllMuscleGroups()).thenReturn(liveMuscleGroups);
        assertEquals(liveMuscleGroups, repository.getMuscleGroups());
        verify(dao).getAllMuscleGroups();
    }

    @Test
    public void getMuscleGroupsBySide() {
        LiveData<List<MuscleGroup>> muscleGroups = Models.createLiveMuscleGroups(1L);
        when(dao.getMuscleGroups(true)).thenReturn(muscleGroups);
        assertEquals(muscleGroups, repository.getMuscleGroups(true));
        verify(dao).getMuscleGroups(true);
    }

    @Test
    public void insertMuscleGroups() {
        final List<MuscleGroup> muscleGroups = Models.createMuscleGroups(1L);
        repository.insertMuscleGroups(muscleGroups);
        verify(dao).insertMuscleGroups(muscleGroups);
    }

    @Test
    public void getMuscleGroupById() {
        final LiveData<MuscleGroup> muscleGroup = Models.createLiveMuscleGroup(1L, "foo");
        when(dao.getMuscleGroupById(1L)).thenReturn(muscleGroup);
        assertEquals(muscleGroup, repository.getMuscleGroupById(1L));
        verify(dao).getMuscleGroupById(1L);
    }

    @Test
    public void getPrimaryMuscleGroupForExercise() {
        repository.getPrimaryMuscleGroupForExercise(100L);
        verify(dao).getPrimaryMuscleGroupForExercise(100L);
    }

    @Test
    public void getSecondaryMuscleGroupsForExercise() {
        List<MuscleGroup> just = Models.createMuscleGroups(2L);
        when(dao.getSecondaryMuscleGroupsForExercise(1L)).thenReturn(just);
        assertEquals(just, repository.getSecondaryMuscleGroupsForExercise(1L));
        verify(dao).getSecondaryMuscleGroupsForExercise(1L);
    }
}