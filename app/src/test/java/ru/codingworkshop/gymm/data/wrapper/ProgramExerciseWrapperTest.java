package ru.codingworkshop.gymm.data.wrapper;

import com.google.common.collect.Lists;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ru.codingworkshop.gymm.data.entity.ExerciseEntity;
import ru.codingworkshop.gymm.data.entity.ProgramSetEntity;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Радик on 26.06.2017.
 */

@RunWith(JUnit4.class)
public class ProgramExerciseWrapperTest {
    @Test
    public void test() {
        ProgramExerciseWrapper wrapper = new ProgramExerciseWrapper();
        wrapper.setExercise(new ExerciseEntity());
        wrapper.setProgramSets(Lists.newArrayList(new ProgramSetEntity()));

        assertNotNull(wrapper.getExercise());
        assertEquals(1, wrapper.getProgramSets().size());
    }
}
