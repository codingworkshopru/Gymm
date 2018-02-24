package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import org.junit.Test;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.repository.ExercisesRepository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExerciseQueryAdapterTest {

    @Test
    public void query() {
        ExercisesRepository repo = mock(ExercisesRepository.class);
        Exercise exercise = new Exercise();
        when(repo.getExerciseById(100L)).thenReturn(Flowable.just(exercise));
        new ExerciseQueryAdapter(repo).query(100L).test().assertValue(exercise);
    }
}