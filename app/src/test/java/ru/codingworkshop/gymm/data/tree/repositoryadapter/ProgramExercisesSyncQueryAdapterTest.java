package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProgramExercisesSyncQueryAdapterTest {

    @Test
    public void query() {
        ProgramTrainingRepository repo = mock(ProgramTrainingRepository.class);
        List<ProgramExercise> programExerciseList = new ArrayList<>();
        when(repo.getProgramExercisesForTrainingSync(1L)).thenReturn(programExerciseList);

        ProgramExercisesSyncQueryAdapter adapter = new ProgramExercisesSyncQueryAdapter(repo);
        assertEquals(programExerciseList, adapter.query(1L));
        verify(repo).getProgramExercisesForTrainingSync(1L);
    }
}