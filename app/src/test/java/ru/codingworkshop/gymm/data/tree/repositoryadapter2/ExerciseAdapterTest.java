package ru.codingworkshop.gymm.data.tree.repositoryadapter2;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Maybe;
import io.reactivex.Single;
import ru.codingworkshop.gymm.db.dao.ExerciseDao;
import ru.codingworkshop.gymm.db.dao.MuscleGroupDao;
import ru.codingworkshop.gymm.util.Models;

import static org.mockito.Mockito.when;

/**
 * Created by Radik on 17.12.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ExerciseAdapterTest {
    @Mock private ExerciseDao exerciseDao;
    @Mock private MuscleGroupDao muscleGroupDao;
    @InjectMocks private ExerciseAdapter adapter;

    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void getPrimaryMuscleGroup() throws Exception {
        when(muscleGroupDao.getPrimaryMuscleGroupForExerciseRx(100L))
                .thenReturn(Single.just(Models.createMuscleGroup(200L, "bar")));

        adapter.getPrimaryMuscleGroup(100L).test().assertValue(mg -> mg.getId() == 200L);
    }

    @Test
    public void getParent() throws Exception {
        when(exerciseDao.getExerciseByIdRx(100L)).thenReturn(Single.just(Models.createExercise(100L, "foo")));

        adapter.getParent(100L).test().assertValue(mg -> mg.getId() == 100L);
    }

    @Test
    public void getChildren() throws Exception {
        when(muscleGroupDao.getSecondaryMuscleGroupsForExerciseRx(100L)).thenReturn(Maybe.just(Models.createMuscleGroups(201L)));

        adapter.getChildren(100L).test().assertValue(mgs -> !mgs.isEmpty());
    }

}