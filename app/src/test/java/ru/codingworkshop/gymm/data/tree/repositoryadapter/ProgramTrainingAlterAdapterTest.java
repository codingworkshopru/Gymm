package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProgramTrainingAlterAdapterTest {

    @Mock private ProgramTrainingRepository repo;
    @InjectMocks private ProgramTrainingAlterAdapter adapter;

    private ProgramTraining programTraining = new ProgramTraining();

    @Test
    public void insert() {
        when(repo.insertProgramTraining(programTraining)).thenReturn(1L);
        assertEquals(1L, adapter.insert(programTraining));
        verify(repo).insertProgramTraining(programTraining);
    }

    @Test
    public void update() {
        when(repo.updateProgramTraining(programTraining)).thenReturn(1);
        assertEquals(1, adapter.update(programTraining));
        verify(repo).updateProgramTraining(programTraining);
    }

    @Test
    public void delete() {
        when(repo.deleteProgramTraining(programTraining)).thenReturn(1);
        assertEquals(1, adapter.delete(programTraining));
        verify(repo).deleteProgramTraining(programTraining);
    }
}