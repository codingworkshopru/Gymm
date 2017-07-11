package ru.codingworkshop.gymm.data.wrapper;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

import ru.codingworkshop.gymm.data.entity.ExerciseEntity;
import ru.codingworkshop.gymm.data.entity.MuscleGroupEntity;
import ru.codingworkshop.gymm.data.model.Exercise;
import ru.codingworkshop.gymm.data.model.MuscleGroup;
import ru.codingworkshop.gymm.db.dao.ExerciseDao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 19.06.2017.
 */

@RunWith(JUnit4.class)
public class ExerciseWrapperTest {
    private Exercise exercise;
    private MutableLiveData<ExerciseEntity> liveExercise;
    private ExerciseDao exerciseDao;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void init() {
        exercise = Mockito.mock(ExerciseEntity.class);
        when(exercise.getName()).thenReturn("foo");

        liveExercise = new MutableLiveData<>();
        liveExercise.setValue((ExerciseEntity) exercise);

        MutableLiveData<List<MuscleGroupEntity>> liveMuscleGroups = new MutableLiveData<>();
        MuscleGroupEntity muscleGroupEntity = new MuscleGroupEntity("bar");
        liveMuscleGroups.setValue(Lists.newArrayList(muscleGroupEntity));

        exerciseDao = mock(ExerciseDao.class);
        when(exerciseDao.getExerciseById(1)).thenReturn(liveExercise);
        when(exerciseDao.getSecondaryMuscleGroupsForExercise(1)).thenReturn(liveMuscleGroups);
    }

    @Test
    public void loadExercise() {
        LiveData<ExerciseWrapper> liveWrapper = ExerciseWrapper.load(1, exerciseDao);
        Observer<ExerciseWrapper> observer = mock(Observer.class);
        liveWrapper.observeForever(observer);
        verify(observer).onChanged(argThat(wrapper ->
            wrapper.getExercise().getName().equals("foo") &&
                wrapper.getSecondaryMuscleGroupsCount() == 1));
        verify(exerciseDao).getExerciseById(1);
        verify(exerciseDao).getSecondaryMuscleGroupsForExercise(1);
        liveExercise.setValue(new ExerciseEntity());
        verify(observer, times(2)).onChanged(any());
    }

    @Test
    public void createExercise() {
        LiveData<ExerciseWrapper> liveWrapper = ExerciseWrapper.create();
        assertEquals(null, liveWrapper.getValue().getExercise().getName());
        assertFalse(liveWrapper.getValue().hasSecondaryMuscleGroups());
    }

    @Test
    public void addition() {
        MuscleGroup muscleGroup = new MuscleGroupEntity("bar");
        ExerciseWrapper wrapper = new ExerciseWrapper(exercise);
        Set<MuscleGroup> muscleGroupSet = wrapper.getSecondaryMuscleGroups();
        wrapper.addSecondaryMuscleGroup(muscleGroup);

        assertTrue("list is empty", wrapper.hasSecondaryMuscleGroups());
        assertEquals("bar", muscleGroupSet.stream().findFirst().get().getName());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetsImmutability() {
        new ExerciseWrapper().getSecondaryMuscleGroups().add(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNull() {
        new ExerciseWrapper(exercise).addSecondaryMuscleGroup(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNull() {
        new ExerciseWrapper(exercise).removeSecondaryMuscleGroup(null);
    }

    @Test
    public void deletion() {
        MuscleGroup muscleGroup = new MuscleGroupEntity("foo");
        ExerciseWrapper wrapper = new ExerciseWrapper(exercise);
        wrapper.addSecondaryMuscleGroup(muscleGroup);
        wrapper.removeSecondaryMuscleGroup(new MuscleGroupEntity("foo"));

        assertFalse(wrapper.hasSecondaryMuscleGroups());
    }
}
