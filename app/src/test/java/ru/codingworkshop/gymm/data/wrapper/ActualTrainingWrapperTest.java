package ru.codingworkshop.gymm.data.wrapper;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import com.google.common.collect.Lists;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.repository.ActualTrainingRepository;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.LiveTest;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 29.07.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ActualTrainingWrapperTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ProgramTrainingRepository programTrainingRepository;
    @Mock private ExercisesRepository exercisesRepository;
    @Mock private ActualTrainingRepository actualTrainingRepository;

    @Test
    public void actualTrainingCreation() {
        ActualTraining training = ActualTrainingWrapper.createActualTraining(11L);
        assertEquals(11L, training.getProgramTrainingId().longValue());
    }

    @Test
    public void creationTest() {
        List<Exercise> exercises = createExercises("foo", "bar", "baz");

        ActualTraining training = new ActualTraining();
        training.setId(11L);
        training.setProgramTrainingId(1L);
        training.setStartTime(new Date());

        LiveData<ActualTraining> liveTraining = LiveDataUtil.getLive(training);

        when(actualTrainingRepository.insertActualTraining(any(ActualTraining.class))).thenReturn(LiveDataUtil.getLive(11L));
        when(actualTrainingRepository.getActualTrainingById(11L)).thenReturn(liveTraining);
        when(exercisesRepository.getExercisesForProgramTraining(1L)).thenReturn(LiveDataUtil.getLive(exercises));

        LiveData<ActualTrainingWrapper> liveWrapper = ActualTrainingWrapper.create(actualTrainingRepository, exercisesRepository, 1L);
        LiveTest.verifyLiveData(
                liveWrapper,
                wrapper -> exercises == wrapper.getExercises()
                        && wrapper.getActualTraining().getId() == 11L
        );

        verify(exercisesRepository).getExercisesForProgramTraining(1L);
        verify(actualTrainingRepository).insertActualTraining(any(ActualTraining.class));
        verify(actualTrainingRepository).getActualTrainingById(11L);
    }

    public List<Exercise> createExercises(String... exerciseNames) {
        List<String> names = Lists.newArrayList(exerciseNames);
        return names.stream().map(name -> {
            Exercise exercise = new Exercise();
            exercise.setId((names.indexOf(name) + 1L));
            exercise.setName(name);
            return exercise;
        }).collect(Collectors.toList());
    }
}
