package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProgramSetsQueryAdapterTest {

    @Test
    public void query() {
        List<ProgramSet> programSets = new ArrayList<>();
        ProgramTrainingRepository repo = mock(ProgramTrainingRepository.class);
        when(repo.getProgramSetsForTraining(1L)).thenReturn(Flowable.just(programSets));

        new ProgramSetsQueryAdapter(repo).query(1L).test().assertValue(programSets);
        verify(repo).getProgramSetsForTraining(1L);
    }
}