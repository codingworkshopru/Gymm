package ru.codingworkshop.gymm.db.initializer;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.Lazy;
import ru.codingworkshop.gymm.BuildConfig;
import ru.codingworkshop.gymm.data.ExerciseDifficulty;
import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.db.dao.ActualTrainingDao;
import ru.codingworkshop.gymm.db.dao.ExerciseDao;
import ru.codingworkshop.gymm.db.dao.MuscleGroupDao;

/**
 * Created by Radik on 12.12.2017.
 */

public class FakeDataInitializer implements Initializer {
    private Lazy<ActualTrainingDao> actualTrainingDao;
    private Lazy<MuscleGroupDao> muscleGroupDao;
    private Lazy<ExerciseDao> exerciseDao;

    @Inject
    FakeDataInitializer(Lazy<ActualTrainingDao> actualTrainingDao, Lazy<MuscleGroupDao> muscleGroupDao, Lazy<ExerciseDao> exerciseDao) {
        this.actualTrainingDao = actualTrainingDao;
        this.muscleGroupDao = muscleGroupDao;
        this.exerciseDao = exerciseDao;
    }

    @Override
    public void initialize() {
        if (!BuildConfig.DEBUG) {
            return;
        }

        final String trainingName = "Plot testing training";
        final String exerciseName = "Plot testing exercise";
        final int trainingDaysCount = 400;
        final int setsPerTraining = 4;
        final int repsPerSet = 10;
        final double weightPerSet = 50f;

        if (muscleGroupDao.get().getMuscleGroupByExerciseNameSync(exerciseName) != null) {
            return;
        }

        MuscleGroup primaryMuscleGroup = new MuscleGroup("foo");
        primaryMuscleGroup.setId(100500L);
        muscleGroupDao.get().insertMuscleGroups(Collections.singletonList(primaryMuscleGroup));

        Exercise e = new Exercise();
        e.setPrimaryMuscleGroupId(primaryMuscleGroup.getId());
        e.setName(exerciseName);
        e.setDifficulty(ExerciseDifficulty.EASY);
        e.setSteps("steps");

        exerciseDao.get().insertExercises(Collections.singletonList(e));

        final long startTs = new Date().getTime() - TimeUnit.DAYS.toMillis(400);
        Collection<ActualSet> sets = new LinkedList<>();
        for (int i = 0; i < trainingDaysCount; i++) {
            ActualTraining actualTraining = new ActualTraining(null, trainingName);
            long trainingStartTime = startTs + TimeUnit.DAYS.toMillis(i);
            actualTraining.setStartTime(new Date(trainingStartTime));
            actualTraining.setFinishTime(new Date(trainingStartTime + TimeUnit.HOURS.toMillis(2)));
            long trainingId = actualTrainingDao.get().insertActualTraining(actualTraining);

            ActualExercise actualExercise = new ActualExercise(exerciseName, trainingId, null);

            long exerciseId = actualTrainingDao.get().insertActualExercise(actualExercise);

            for (int j = 0; j < setsPerTraining; j++) {
                ActualSet set = new ActualSet(exerciseId, repsPerSet + (int)(repsPerSet/2.0 * Math.random()));
                double delta = weightPerSet - (Math.random() * weightPerSet);
                double trend = i > 0 ? 10*Math.log(i) : 0;
//                set.setWeight(weightPerSet + trend + delta);
                sets.add(set);
            }
        }
        actualTrainingDao.get().insertActualSets(sets);
    }
}
