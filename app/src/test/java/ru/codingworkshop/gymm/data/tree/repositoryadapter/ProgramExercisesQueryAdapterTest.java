package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import org.junit.Test;

import java.util.ArrayList;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProgramExercisesQueryAdapterTest {

    @Test
    public void query() {
        ProgramTrainingRepository repo = mock(ProgramTrainingRepository.class);
        ArrayList<ProgramExercise> programExercises = new ArrayList<>();
        when(repo.getProgramExercisesForTraining(1L)).thenReturn(programExercises);

        ProgramExercisesQueryAdapter adapter = new ProgramExercisesQueryAdapter(repo);
        assertEquals(programExercises, adapter.query(1L));
        verify(repo).getProgramExercisesForTraining(1L);
    }
}