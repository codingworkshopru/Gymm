package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import org.junit.Test;

import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.repository.MuscleGroupsRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PrimaryMuscleGroupQueryAdapterTest {

    @Test
    public void query() {
        MuscleGroupsRepository repo = mock(MuscleGroupsRepository.class);
        MuscleGroup muscleGroup = new MuscleGroup("foo");
        when(repo.getPrimaryMuscleGroupForExercise(100L)).thenReturn(muscleGroup);
        PrimaryMuscleGroupQueryAdapter adapter = new PrimaryMuscleGroupQueryAdapter(repo);
        assertEquals(muscleGroup, adapter.query(100L));
        verify(repo).getPrimaryMuscleGroupForExercise(100L);
    }
}