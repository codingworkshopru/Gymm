package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import org.junit.Test;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.repository.MuscleGroupsRepository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PrimaryMuscleGroupQueryAdapterTest {

    @Test
    public void query() {
        MuscleGroupsRepository repo = mock(MuscleGroupsRepository.class);
        MuscleGroup muscleGroup = new MuscleGroup("foo");
        when(repo.getPrimaryMuscleGroupForExercise(100L)).thenReturn(Flowable.just(muscleGroup));
        PrimaryMuscleGroupQueryAdapter adapter = new PrimaryMuscleGroupQueryAdapter(repo);
        adapter.query(100L).test().assertValue(muscleGroup);
        verify(repo).getPrimaryMuscleGroupForExercise(100L);
    }
}