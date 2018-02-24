package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import org.junit.Test;

import java.util.List;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.repository.MuscleGroupsRepository;
import ru.codingworkshop.gymm.util.Models;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SecondaryMuscleGroupsQueryAdapterTest {

    @Test
    public void query() {
        MuscleGroupsRepository repo = mock(MuscleGroupsRepository.class);
        List<MuscleGroup> muscleGroupList = Models.createMuscleGroups(1L);
        when(repo.getSecondaryMuscleGroupsForExercise(100L)).thenReturn(Flowable.just(muscleGroupList));

        SecondaryMuscleGroupsQueryAdapter adapter = new SecondaryMuscleGroupsQueryAdapter(repo);
        adapter.query(100L).test().assertValue(muscleGroupList);
        verify(repo).getSecondaryMuscleGroupsForExercise(100L);
    }
}