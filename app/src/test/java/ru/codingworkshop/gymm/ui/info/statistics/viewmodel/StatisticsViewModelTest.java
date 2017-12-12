package ru.codingworkshop.gymm.ui.info.statistics.viewmodel;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import com.google.common.base.Function;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ru.codingworkshop.gymm.data.entity.ExercisePlotTuple;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.db.dao.ActualTrainingDao;
import ru.codingworkshop.gymm.util.LiveTest;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Radik on 11.12.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class StatisticsViewModelTest {
    @Mock private ActualTrainingDao dao;
    @InjectMocks private StatisticsViewModel vm;

    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        List<String> foo = Collections.singletonList("foo");
        LiveData<List<String>> live = LiveDataUtil.getLive(foo);
        when(dao.getActualExerciseNames()).thenReturn(live);
        ExercisePlotTuple tuple = new ExercisePlotTuple();
        tuple.setReps(10);
        tuple.setWeight(10d);
        tuple.setTrainingTime(new Date());
    }

    @Test
    public void queryStatistics() throws Exception {
        vm.chartEntries.observeForever(l -> {});
        vm.exerciseId.setValue(0L);
        vm.dataTypeId.setValue(0L);
        verify(dao, never()).getStatisticsForExerciseSync(anyString(), any(Date.class));
        vm.getActualExerciseNames();
        vm.rangeId.setValue(5L);
        LiveTest.getValue(vm.chartEntries);
        verify(dao).getStatisticsForExerciseSync("foo", null);
    }

    @Test
    public void getExercisesTest() throws Exception {
        when(dao.getActualExerciseNames())
                .thenReturn(LiveDataUtil.getAbsent())
                .thenReturn(LiveDataUtil.getAbsent());

        LiveData<List<String>> actualExerciseNames = vm.getActualExerciseNames();
        assertSame(actualExerciseNames, vm.getActualExerciseNames());

        verify(dao).getActualExerciseNames();
    }

    @Test
    public void getExerciseNameById() throws Exception {
        vm.getActualExerciseNames();

        assertEquals("foo", vm.getExerciseNameById(0L));
    }

    @Test
    public void getStartDateById() throws Exception {
        Calendar c = new GregorianCalendar(Locale.getDefault());
        c.add(Calendar.MONTH, -1);
        DateFormat f = DateFormat.getInstance();
        assertEquals(f.format(c.getTime()), f.format(vm.getStartDateById(0L)));
        assertNull(vm.getStartDateById(5L));
    }

    @Test
    public void getDataFunctionById() throws Exception {
        ExercisePlotTuple tuple = new ExercisePlotTuple();
        tuple.setWeight(2.0);
        tuple.setReps(2);
        assertEquals(4.0, vm.getDataFunctionById(0L).apply(tuple));
    }
}