package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProgramSetsSyncQueryAdapterTest {

    @Test
    public void query() {
        ProgramTrainingRepository repo = mock(ProgramTrainingRepository.class);
        List<ProgramSet> sets = new ArrayList<>();
        when(repo.getProgramSetsForTrainingSync(1L)).thenReturn(sets);

        ProgramSetsSyncQueryAdapter adapter = new ProgramSetsSyncQueryAdapter(repo);
        assertEquals(sets, adapter.query(1L));
        verify(repo).getProgramSetsForTrainingSync(1L);
    }
}