package ru.codingworkshop.gymm.repository;

import android.support.annotation.WorkerThread;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.data.entity.SecondaryMuscleGroupLink;
import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.entity.common.Named;
import ru.codingworkshop.gymm.db.dao.ExerciseDao;
import ru.codingworkshop.gymm.db.dao.MuscleGroupDao;

/**
 * Created by Радик on 18.06.2017.
 */

@WorkerThread
final class SecondaryMuscleGroupsHelper {

    private ExerciseDao exerciseDao;
    private MuscleGroupDao muscleGroupDao;

    SecondaryMuscleGroupsHelper(ExerciseDao exerciseDao, MuscleGroupDao muscleGroupDao) {
        this.exerciseDao = exerciseDao;
        this.muscleGroupDao = muscleGroupDao;
    }

    void updateLinks(Exercise exercise, List<MuscleGroup> secondaryMuscleGroups) {
        long exerciseId = exercise.getId();
        if (exerciseId == 0)
            throw new RuntimeException("Exercise doesn't exist: " + exercise.getName());

        List<SecondaryMuscleGroupLink> oldLinksList =
                exerciseDao.getSecondaryMuscleGroupLinkSync(exerciseId);
        List<SecondaryMuscleGroupLink> newLinksList =
                createLinkInstances(exercise, secondaryMuscleGroups);

        Set<SecondaryMuscleGroupLink> oldLinks = Sets.newHashSet(oldLinksList);
        Set<SecondaryMuscleGroupLink> newLinks = Sets.newHashSet(newLinksList);

        exerciseDao.createLinks(Lists.newArrayList(Sets.difference(newLinks, oldLinks)));
        exerciseDao.deleteLinks(Lists.newArrayList(Sets.difference(oldLinks, newLinks)));
    }

    void createExercises(List<Exercise> exercises) {
        Map<String, Long> nameIdMuscleGroupsMap = createNameIdMap(muscleGroupDao.getAllMuscleGroupsSync());
        for (Exercise exercise : exercises)
            exercise.setPrimaryMuscleGroupId(nameIdMuscleGroupsMap.get(exercise.primaryMuscle));
        exerciseDao.insertExercises(exercises);

        Map<String, Long> nameIdExercisesMap = createNameIdMap(exerciseDao.getAllExercisesSync());

        List<SecondaryMuscleGroupLink> links = new LinkedList<>();
        for (Exercise exercise : exercises) {
            if (exercise.secondaryMuscles == null) continue;
            links.addAll(
                    Lists.transform(exercise.secondaryMuscles, (name) ->
                            new SecondaryMuscleGroupLink(
                                    nameIdExercisesMap.get(exercise.getName()),
                                    nameIdMuscleGroupsMap.get(name)
                            )
                    )
            );
        }

        exerciseDao.createLinks(links);
    }

    private static Map<String, Long> createNameIdMap(List<? extends Model> entities) {
        Map<String, Long> result = Maps.newHashMapWithExpectedSize(entities.size());
        for (Model entity : entities)
            result.put(((Named) entity).getName(), entity.getId());
        return result;
    }

    private static List<SecondaryMuscleGroupLink> createLinkInstances(Exercise e, List<? extends MuscleGroup> muscleGroups) {
        return Lists.transform(
                muscleGroups,
                (mg) -> new SecondaryMuscleGroupLink(e.getId(), mg.getId())
        );
    }
}
