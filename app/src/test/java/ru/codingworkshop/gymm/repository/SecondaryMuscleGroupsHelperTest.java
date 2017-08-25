package ru.codingworkshop.gymm.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.stream.Collectors;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.data.entity.SecondaryMuscleGroupLink;
import ru.codingworkshop.gymm.db.dao.ExerciseDao;
import ru.codingworkshop.gymm.db.dao.MuscleGroupDao;
import ru.codingworkshop.gymm.util.Models;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 27.07.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class SecondaryMuscleGroupsHelperTest {
    @Mock private ExerciseDao exerciseDao;
    @Mock private MuscleGroupDao muscleGroupDao;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void createMuscleGroupExerciseLinksTest() {
        List<MuscleGroup> muscleGroups = Models.createMuscleGroups(200L, 300L, 400L);
        List<Exercise> exercises = Models.createExercises("a", "c", "z", "f");
        List<SecondaryMuscleGroupLink> links = Lists.newArrayList();
        exercises.forEach(e -> {
            e.primaryMuscle = muscleGroups.get(0).getName();

            final List<MuscleGroup> muscleGroupsForThisExercise = muscleGroups.subList(exercises.indexOf(e), muscleGroups.size());
            if (!muscleGroupsForThisExercise.isEmpty()) {
                e.secondaryMuscles = muscleGroupsForThisExercise.stream().map(MuscleGroup::getName).collect(Collectors.toList());
            }
            links.addAll(
                    muscleGroupsForThisExercise.stream().map(
                            mg -> new SecondaryMuscleGroupLink(e.getId(), mg.getId())
                    ).collect(Collectors.toList())
            );
        });

        when(muscleGroupDao.getAllMuscleGroupsSync()).thenReturn(muscleGroups);
        when(exerciseDao.insertExercises(exercises)).thenReturn(exercises.stream().map(Exercise::getId).collect(Collectors.toList()));

        SecondaryMuscleGroupsHelper helper = new SecondaryMuscleGroupsHelper(exerciseDao, muscleGroupDao);
        helper.createExercises(exercises);

        verify(exerciseDao).createLinks(argThat(createdLinks -> Sets.newHashSet(createdLinks).equals(Sets.newHashSet(links))));

        verify(exerciseDao).insertExercises(exercises);
        verify(muscleGroupDao).getAllMuscleGroupsSync();
    }
}
