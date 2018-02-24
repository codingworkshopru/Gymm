package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProgramExercisesAlterAdapterTest {
    @Mock private ProgramTrainingRepository repo;
    @InjectMocks private ProgramExercisesAlterAdapter adapter;

    private List<ProgramExercise> exercises = new ArrayList<>();

    @Test
    public void insert() {
        List<Long> ids = new ArrayList<>();
        when(repo.insertProgramExercises(exercises)).thenReturn(ids);
        assertEquals(ids, adapter.insert(exercises));
        verify(repo).insertProgramExercises(exercises);
    }

    @Test
    public void update() {
        when(repo.updateProgramExercises(exercises)).thenReturn(1);
        assertEquals(1, adapter.update(exercises));
        verify(repo).updateProgramExercises(exercises);
    }

    @Test
    public void delete() {
        when(repo.deleteProgramExercises(exercises)).thenReturn(1);
        assertEquals(1, adapter.delete(exercises));
        verify(repo).deleteProgramExercises(exercises);
    }
}