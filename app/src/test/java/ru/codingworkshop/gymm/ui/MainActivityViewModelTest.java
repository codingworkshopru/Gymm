package ru.codingworkshop.gymm.ui;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.Models;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Radik on 22.11.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class MainActivityViewModelTest {
    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ProgramTrainingRepository repository;
    private MainActivityViewModel vm;

    @Before
    public void setUp() throws Exception {
        when(repository.getProgramTrainings())
                .thenReturn(Models.createLiveProgramTrainings(1L))
                .thenReturn(Models.createLiveProgramTrainings(2L));
        vm = new MainActivityViewModel(repository);
    }

    @Test
    public void load() throws Exception {
        LiveData<List<ProgramTraining>> programTrainings = vm.load();
        assertNotNull(programTrainings);
        assertSame(programTrainings, vm.load());
        verify(repository).getProgramTrainings();
    }
}