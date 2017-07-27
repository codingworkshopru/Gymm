package ru.codingworkshop.gymm.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.stream.Collectors;

import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.db.dao.MuscleGroupDao;

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
        List<MuscleGroup> groups = Lists.newArrayList(1L, 2L, 3L).stream().map(id -> createMuscleGroup(id, "foo" + id)).collect(Collectors.toList());
        LiveData<List<MuscleGroup>> liveGroups = LiveDataUtil.getLive(groups);

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
        List<MuscleGroup> muscleGroups = Lists.newArrayList(createMuscleGroup(1L, "foo"));
        repository.insertMuscleGroups(muscleGroups);
        verify(dao).insertMuscleGroups(muscleGroups);
    }

    private MuscleGroup createMuscleGroup(long id, String name) {
        MuscleGroup muscleGroup = new MuscleGroup(name);
        muscleGroup.setId(id);
        return muscleGroup;
    }
}
