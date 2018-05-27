package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import org.junit.Test;

import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProgramTrainingQueryAdapterTest {

    @Test
    public void query() {
        ProgramTrainingRepository repo = mock(ProgramTrainingRepository.class);
        ProgramTraining programTraining = new ProgramTraining();
        when(repo.getProgramTrainingById(1L)).thenReturn(programTraining);

        ProgramTrainingQueryAdapter adapter = new ProgramTrainingQueryAdapter(repo);
        assertEquals(programTraining, adapter.query(1L));
        verify(repo).getProgramTrainingById(1L);
    }
}