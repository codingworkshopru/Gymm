package ru.codingworkshop.gymm.ui.info.statistics.plot;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.entity.ExercisePlotTuple;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.repository.ActualTrainingRepository;
import ru.codingworkshop.gymm.util.LiveTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Radik on 11.12.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class StatisticsPlotViewModelTest {
    @Mock private ActualTrainingRepository repository;
    @InjectMocks private StatisticsPlotViewModel vm;

    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        List<String> foo = Collections.singletonList("foo");
        LiveData<List<String>> live = LiveDataUtil.getLive(foo);
        when(repository.getActualExerciseNames()).thenReturn(live);
    }

    @Test
    public void queryStatistics() throws Exception {
        ExercisePlotTuple tuple = new ExercisePlotTuple();
        tuple.setTrainingTime(new Date());
        tuple.setReps(1);
        tuple.setWeight(1.0);
        when(repository.getStatisticsForExercise("foo", null)).thenReturn(Collections.singletonList(tuple));
        vm.getChartEntries().observeForever(l -> {});
        vm.getExerciseId().setValue(0L);
        vm.getDataTypeId().setValue(0L);
        verify(repository, never()).getStatisticsForExercise(anyString(), any(Date.class));
        vm.getActualExerciseNames();
        vm.getRangeId().setValue(5L);
        LiveTest.getValue(vm.getChartEntries());
        verify(repository).getStatisticsForExercise("foo", null);
    }

    @Test
    public void getActualExerciseNames() {
        when(repository.getActualExerciseNames())
                .thenReturn(LiveDataUtil.getAbsent())
                .thenReturn(LiveDataUtil.getAbsent());

        LiveData<List<String>> actualExerciseNames = vm.getActualExerciseNames();
        assertSame(actualExerciseNames, vm.getActualExerciseNames());

        verify(repository).getActualExerciseNames();
    }

    @Test
    public void getExerciseNameById() {
        vm.getActualExerciseNames();

        assertEquals("foo", vm.getExerciseNameById(0L));
    }

    @Test
    public void getStartDateById() {
        Calendar c = new GregorianCalendar(Locale.getDefault());
        c.add(Calendar.MONTH, -1);
        DateFormat f = DateFormat.getInstance();
        assertEquals(f.format(c.getTime()), f.format(StatisticsPlotViewModel.getStartDateById(0L)));
        assertNull(StatisticsPlotViewModel.getStartDateById(5L));
    }

    @Test
    public void getDataFunctionById() {
        ExercisePlotTuple tuple = new ExercisePlotTuple();
        tuple.setWeight(2.0);
        tuple.setReps(2);
        assertEquals(4.0, StatisticsPlotViewModel.getDataFunctionById(0L).apply(tuple));
    }
}