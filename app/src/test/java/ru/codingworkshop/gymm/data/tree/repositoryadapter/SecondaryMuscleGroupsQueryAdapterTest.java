package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import org.junit.Test;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.repository.MuscleGroupsRepository;
import ru.codingworkshop.gymm.util.Models;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SecondaryMuscleGroupsQueryAdapterTest {

    @Test
    public void query() {
        MuscleGroupsRepository repo = mock(MuscleGroupsRepository.class);
        List<MuscleGroup> muscleGroupList = Models.createMuscleGroups(1L);
        when(repo.getSecondaryMuscleGroupsForExercise(100L)).thenReturn(muscleGroupList);

        SecondaryMuscleGroupsQueryAdapter adapter = new SecondaryMuscleGroupsQueryAdapter(repo);
        assertEquals(muscleGroupList, adapter.query(100L));
        verify(repo).getSecondaryMuscleGroupsForExercise(100L);
    }
}