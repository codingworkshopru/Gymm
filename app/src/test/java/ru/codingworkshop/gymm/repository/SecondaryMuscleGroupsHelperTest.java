package ru.codingworkshop.gymm.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.common.collect.Lists;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.data.entity.SecondaryMuscleGroupLink;
import ru.codingworkshop.gymm.db.dao.ExerciseDao;
import ru.codingworkshop.gymm.db.dao.MuscleGroupDao;

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
        List<String> muscleNames = Lists.newArrayList("foo", "bar", "baz");
        List<MuscleGroup> muscleGroups = Lists.newArrayList();
        muscleNames.forEach(name -> {
            MuscleGroup mg = new MuscleGroup(name);
            mg.setId(name.hashCode());
            muscleGroups.add(mg);
        });

        List<Exercise> exercises = Lists.newArrayList();
        Lists.newArrayList("a", "c", "z", "f").forEach(name -> {
            Exercise e = new Exercise();
            e.setName(name);
            e.setId(name.hashCode());
            e.primaryMuscle = "foo";
            exercises.add(e);
        });

        List<SecondaryMuscleGroupLink> links = Lists.newArrayList();

        for (int i = 0; i < muscleNames.size(); i++) {
            List<String> muscleGroupsForExercise = Lists.newArrayList();
            muscleGroupsForExercise.addAll(muscleNames.subList(i, 3));
            exercises.get(i).secondaryMuscles = muscleGroupsForExercise;
            final int j = i;
            muscleGroups.subList(i, 3).forEach(mg -> links.add(
                    new SecondaryMuscleGroupLink(exercises.get(j).getId(), mg.getId())
            ));
        }

        when(muscleGroupDao.getAllMuscleGroupsSync()).thenReturn(muscleGroups);
        when(exerciseDao.insertExercises(exercises)).thenReturn(exercises.stream().map(e -> e.getId()).collect(Collectors.toList()));

        SecondaryMuscleGroupsHelper helper = new SecondaryMuscleGroupsHelper(exerciseDao, muscleGroupDao);
        helper.createExercises(exercises);

        verify(exerciseDao).createLinks(argThat(ls ->
            ls.stream().allMatch(secondaryMuscleGroupLink -> {
                return links.stream().anyMatch(secondaryMuscleGroupLink2 -> secondaryMuscleGroupLink.getExerciseId() == secondaryMuscleGroupLink2.getExerciseId()
                        && secondaryMuscleGroupLink.getMuscleGroupId() == secondaryMuscleGroupLink2.getMuscleGroupId());
            })
        ));
        verify(exerciseDao).insertExercises(exercises);
        verify(muscleGroupDao).getAllMuscleGroupsSync();
    }
}
