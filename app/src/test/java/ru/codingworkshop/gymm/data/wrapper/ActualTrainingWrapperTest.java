package ru.codingworkshop.gymm.data.wrapper;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.util.ModelsFixture;

import static org.junit.Assert.assertEquals;

/**
 * Created by Радик on 06.08.2017 as part of the Gymm project.
 */

@RunWith(JUnit4.class)
public class ActualTrainingWrapperTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private ActualTrainingWrapper wrapper;

    @Before
    public void init() {
        wrapper = new ActualTrainingWrapper();
    }

    @Test
    public void programTrainingGetAndSet() {
        ProgramTraining training = ModelsFixture.createProgramTraining(1L, "foo");
        wrapper.setProgramTraining(training);
        assertEquals(training, wrapper.getProgramTraining());
    }

    @Test(expected = NullPointerException.class)
    public void setNullProgramTraining() {
        wrapper.setProgramTraining(null);
    }

    @Test
    public void programExercisesSetAndGet() {
        List<ProgramExercise> programExercises = ModelsFixture.createProgramExercises(10);
        wrapper.setProgramExercises(programExercises);
        assertEquals(programExercises, wrapper.getProgramExercises());
    }

    @Test(expected = NullPointerException.class)
    public void setNullProgramExercises() {
        wrapper.setProgramExercises(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setEmptyProgramExercises() {
        wrapper.setProgramExercises(Lists.newArrayList());
    }

    @Test
    public void exercisesSetAndGet() {
        List<Exercise> exercises = ModelsFixture.createExercises("foo", "bar", "baz");
        wrapper.setExercises(exercises);
        assertEquals(exercises, wrapper.getExercises());
    }

    @Test(expected = NullPointerException.class)
    public void setNullExercises() {
        wrapper.setExercises(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setEmptyExercises() {
        wrapper.setExercises(Lists.newArrayList());
    }

    @Test
    public void programSetsSetAndGet() {
        List<ProgramSet> sets = ModelsFixture.createProgramSets(10);
        wrapper.setProgramSets(sets);
        assertEquals(sets, wrapper.getProgramSets());
    }

    @Test(expected = NullPointerException.class)
    public void setNullProgramSets() {
        wrapper.setProgramSets(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setEmptyProgramSets() {
        wrapper.setProgramSets(Lists.newArrayList());
    }

    @Test
    public void getProgramSetsForProgramExercise() {
        ProgramExercise exercise = ModelsFixture.createProgramExercise(2L, 1L, 100L, false);
        List<ProgramSet> sets = ModelsFixture.createProgramSets(9);
        wrapper.setProgramExercises(Lists.newArrayList(exercise));
        wrapper.setProgramSets(sets);
        assertEquals(sets, wrapper.getProgramSetsForExercise(exercise));
        assertEquals(sets, wrapper.getProgramSetsForExercise(exercise.getId()));
    }

    @Test(expected = NullPointerException.class)
    public void getProgramSetsForNull() {
        wrapper.getProgramSetsForExercise(null);
    }

    @Test
    public void getExerciseForProgramExercise() {
        ProgramExercise programExercise = ModelsFixture.createProgramExercise(2L, 1L, 100L, false);
        Exercise exercise = ModelsFixture.createExercise(100L, "foo");
        wrapper.setProgramExercises(Lists.newArrayList(programExercise));
        wrapper.setExercises(Lists.newArrayList(exercise));
        assertEquals(exercise, wrapper.getExerciseForProgramExercise(programExercise));
    }

    @Test(expected = NullPointerException.class)
    public void getExerciseForNull() {
        wrapper.getExerciseForProgramExercise(null);
    }
}
