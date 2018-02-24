package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import org.junit.Test;

import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProgramTrainingSyncQueryAdapterTest {

    @Test
    public void query() {
        ProgramTrainingRepository repo = mock(ProgramTrainingRepository.class);
        ProgramTraining programTraining = new ProgramTraining();
        when(repo.getProgramTrainingByIdSync(1L)).thenReturn(programTraining);

        ProgramTrainingSyncQueryAdapter adapter = new ProgramTrainingSyncQueryAdapter(repo);
        assertEquals(programTraining, adapter.query(1L));
        verify(repo).getProgramTrainingByIdSync(1L);
    }
}