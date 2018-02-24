package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProgramSetsAlterAdapterTest {

    @Mock private ProgramTrainingRepository repository;
    @InjectMocks private ProgramSetsAlterAdapter adapter;

    private List<ProgramSet> programSets = new ArrayList<>();

    @Test
    public void insert() {
        List<Long> ids = new ArrayList<>();
        when(repository.insertProgramSets(programSets)).thenReturn(ids);
        assertEquals(ids, adapter.insert(programSets));
        verify(repository).insertProgramSets(programSets);
    }

    @Test
    public void update() {
        when(repository.updateProgramSets(programSets)).thenReturn(1);
        assertEquals(1L, adapter.update(programSets));
        verify(repository).updateProgramSets(programSets);
    }

    @Test
    public void delete() {
        when(repository.deleteProgramSets(programSets)).thenReturn(1);
        assertEquals(1L, adapter.delete(programSets));
        verify(repository).deleteProgramSets(programSets);
    }
}