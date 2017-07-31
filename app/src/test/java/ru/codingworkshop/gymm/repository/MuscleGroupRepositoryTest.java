package ru.codingworkshop.gymm.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.db.dao.MuscleGroupDao;
import ru.codingworkshop.gymm.util.ModelsFixture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 27.07.2017.
 */

@RunWith(JUnit4.class)
public class MuscleGroupRepositoryTest {
    private MuscleGroupDao dao;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void init() {
        dao = mock(MuscleGroupDao.class);
    }

    @Test
    public void getMuscleGroups() {
        LiveData<List<MuscleGroup>> liveGroups = ModelsFixture.createLiveMuscleGroups(1L, 2L, 3L);

        when(dao.getAllMuscleGroups()).thenReturn(liveGroups);

        MuscleGroupsRepository repository = new MuscleGroupsRepository(dao);
        assertEquals(liveGroups, repository.getMuscleGroups());

        verify(dao).getAllMuscleGroups();
    }

    @Test
    public void repositoryEmptiness() {
        when(dao.getMuscleGroupsCount()).thenReturn(0);

        MuscleGroupsRepository repository = new MuscleGroupsRepository(dao);
        assertTrue(repository.isEmpty());

        when(dao.getMuscleGroupsCount()).thenReturn(1000);
        assertFalse(repository.isEmpty());

        verify(dao, times(2)).getMuscleGroupsCount();
    }

    @Test
    public void insertionTest() {
        MuscleGroupsRepository repository = new MuscleGroupsRepository(dao);
        List<MuscleGroup> muscleGroups = ModelsFixture.createMuscleGroups(1L, 2L);
        repository.insertMuscleGroups(muscleGroups);
        verify(dao).insertMuscleGroups(muscleGroups);
    }
}
