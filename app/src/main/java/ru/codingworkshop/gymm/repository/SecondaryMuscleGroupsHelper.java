package ru.codingworkshop.gymm.repository;

import android.support.annotation.WorkerThread;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
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
        Map<String, Long> nameIdMuscleGroupsMap = createMap(muscleGroupDao.getAllMuscleGroupsSync(), Named::getName, Model::getId);
        for (Exercise exercise : exercises)
            exercise.setPrimaryMuscleGroupId(nameIdMuscleGroupsMap.get(exercise.primaryMuscle));
        List<Long> ids = exerciseDao.insertExercises(exercises);

        Iterator<Exercise> exerciseIterator = exercises.iterator();
        Iterator<Long> idIterator = ids.iterator();

        while(exerciseIterator.hasNext() && idIterator.hasNext()) {
            exerciseIterator.next().setId(idIterator.next());
        }

        Map<String, Long> nameIdExercisesMap = createMap(exercises, Named::getName, Model::getId);

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

    private static <K, V, T> Map<K, V> createMap(Collection<T> collection, Function<T, K> keyExtractor, Function<T, V> valueExtractor) {
        Map<K, V> result = Maps.newHashMapWithExpectedSize(collection.size());
        for (T entry : collection) {
            result.put(keyExtractor.apply(entry), valueExtractor.apply(entry));
        }
        return result;
    }
}
