package ru.codingworkshop.gymm.data.wrapper;

import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;

import com.google.common.collect.Lists;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.codingworkshop.gymm.data.model.ProgramExercise;
import ru.codingworkshop.gymm.data.model.common.Model;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 11.07.2017.
 */

@RunWith(JUnit4.class)
public class ChildrenDiffTest {
    @Test
    public void test() {
        List<ProgramExercise> oldList = getModels(1L, 2L, 3L, 4L);
        List<ProgramExercise> newList = getModels(4L, 2L, 5L, 1L, 8L);

        ChildrenDiff<ProgramExercise> diff = new ChildrenDiff<ProgramExercise>(oldList, newList, (a, b) -> (int) (a.getId() - b.getId()), (a, b) -> a.getSortOrder() - b.getSortOrder()) {

            @Override
            public void difference(List<ProgramExercise> toDelete, List<ProgramExercise> toUpdate, List<ProgramExercise> toInsert) {
                assertFalse(toDelete.isEmpty());
                assertFalse(toUpdate.isEmpty());
                assertFalse(toInsert.isEmpty());

                assertTrue(toDelete.stream().allMatch(model -> model.getId() == 3L));
                assertTrue(toUpdate.stream().allMatch(model -> model.getId() == 1L || model.getId() == 4L));
                assertTrue(toInsert.stream().allMatch(model -> model.getId() == 5L || model.getId() == 8L));
            }
        };

        diff.calculate();
    }

    private static List<ProgramExercise> getModels(Long... ids) {
        List<ProgramExercise> result = Lists.newArrayList();
        Lists.newArrayList(ids).forEach(id -> {
                    ProgramExercise model = mock(ProgramExercise.class);
                    when(model.getId()).thenReturn(id);
                    when(model.getSortOrder()).thenReturn(Arrays.asList(ids).indexOf(id));
                    result.add(model);
                }
        );

        return result;
    }
}
