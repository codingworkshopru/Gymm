package ru.codingworkshop.gymm.data.wrapper;

import com.google.common.collect.Lists;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;

import static junit.framework.Assert.*;

/**
 * Created by Радик on 26.06.2017.
 */

@RunWith(JUnit4.class)
public class ProgramExerciseWrapperTest {
    @Test
    public void creation() {
        ProgramExerciseWrapper wrapper = new ProgramExerciseWrapper();
        wrapper.setExercise(createExercise());
        wrapper.setProgramSets(Lists.newArrayList(new ProgramSet()));

        assertNotNull(wrapper.getExercise());
        assertEquals(1, wrapper.getProgramSets().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void phantomExerciseTest() {
        ProgramExerciseWrapper wrapper = new ProgramExerciseWrapper();
        wrapper.setExercise(new Exercise());
    }



    private Exercise createExercise() {
        Exercise exercise = new Exercise();
        exercise.setId(33L);
        return exercise;
    }
}
