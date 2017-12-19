package ru.codingworkshop.gymm.data.tree.repositoryadapter2;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.MuscleGroupsRepository;
import ru.codingworkshop.gymm.util.Models;

import static org.mockito.Mockito.when;

/**
 * Created by Radik on 17.12.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ExerciseAdapterTest {
    @Mock private ExercisesRepository exercisesRepository;
    @Mock private MuscleGroupsRepository muscleGroupsRepository;
    @InjectMocks private ExerciseAdapter adapter;

    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void getPrimaryMuscleGroup() throws Exception {
        when(muscleGroupsRepository.getPrimaryMuscleGroupForExercise(100L))
                .thenReturn(Flowable.just(Models.createMuscleGroup(200L, "bar")));

        adapter.getPrimaryMuscleGroup(100L).test().assertValue(mg -> mg.getId() == 200L);
    }

    @Test
    public void getParent() throws Exception {
        when(exercisesRepository.getExerciseById(100L)).thenReturn(Flowable.just(Models.createExercise(100L, "foo")));

        adapter.getParent(100L).test().assertValue(mg -> mg.getId() == 100L);
    }

    @Test
    public void getChildren() throws Exception {
        when(muscleGroupsRepository.getSecondaryMuscleGroupsForExercise(100L)).thenReturn(Flowable.just(Models.createMuscleGroups(201L)));

        adapter.getChildren(100L).test().assertValue(mgs -> !mgs.isEmpty());
    }

}