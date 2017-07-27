package ru.codingworkshop.gymm.repository;

import android.support.annotation.WorkerThread;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.SecondaryMuscleGroupLink;
import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.entity.common.Named;
import ru.codingworkshop.gymm.db.dao.ExerciseDao;
import ru.codingworkshop.gymm.db.dao.MuscleGroupDao;

/**
 * Created by Радик on 18.06.2017.
 */

final class SecondaryMuscleGroupsHelper {
    private ExerciseDao exerciseDao;
    private MuscleGroupDao muscleGroupDao;

    SecondaryMuscleGroupsHelper(ExerciseDao exerciseDao, MuscleGroupDao muscleGroupDao) {
        this.exerciseDao = exerciseDao;
        this.muscleGroupDao = muscleGroupDao;
    }

    @WorkerThread
    void createExercises(List<Exercise> exercises) {
        Map<String, Long> nameIdMuscleGroupsMap = createNameIdMap(muscleGroupDao.getAllMuscleGroupsSync());
        for (Exercise exercise : exercises)
            exercise.setPrimaryMuscleGroupId(nameIdMuscleGroupsMap.get(exercise.primaryMuscle));
        List<Long> ids = exerciseDao.insertExercises(exercises);

        Iterator<Exercise> exerciseIterator = exercises.iterator();
        Iterator<Long> idIterator = ids.iterator();

        while(exerciseIterator.hasNext() && idIterator.hasNext()) {
            exerciseIterator.next().setId(idIterator.next());
        }

        Map<String, Long> nameIdExercisesMap = createNameIdMap(exercises);

        List<SecondaryMuscleGroupLink> links = Lists.newLinkedList();
        for (Exercise exercise : exercises) {
            if (exercise.secondaryMuscles == null)
                continue;

            for (String muscleName : exercise.secondaryMuscles) {
                long exerciseId = nameIdExercisesMap.get(exercise.getName());
                long muscleGroupId = nameIdMuscleGroupsMap.get(muscleName);
                links.add(new SecondaryMuscleGroupLink(exerciseId, muscleGroupId));
            }

        }

        exerciseDao.createLinks(links);
    }

    private static Map<String, Long> createNameIdMap(List<? extends Model> entities) {
        Map<String, Long> result = Maps.newHashMapWithExpectedSize(entities.size());
        for (Model entity : entities)
            result.put(((Named) entity).getName(), entity.getId());
        return result;
    }
}
