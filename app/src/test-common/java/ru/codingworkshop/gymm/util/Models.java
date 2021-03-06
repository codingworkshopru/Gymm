package ru.codingworkshop.gymm.util;

import android.arch.lifecycle.LiveData;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;

/**
 * Created by Радик on 31.07.2017 as part of the Gymm project.
 */

public class Models {
    public static List<SimpleModel> createSimpleModels(String... names) {
        List<String> namesList = Lists.newArrayList(names);
        return new ArrayList<>(Lists.transform(namesList,
                name -> new SimpleModel(namesList.contains(name) ? namesList.indexOf(name) + 1L : -1L, name)
        ));
    }

    public static List<SimpleModel> createSimpleModels(Long... ids) {
        return createByIds(id -> new SimpleModel(id, "simple model " + id), ids);
    }

    public static LiveData<List<ProgramTraining>> createLiveProgramTrainings(Long... trainingIds) {
        return LiveDataUtil.getLive(createProgramTrainings(trainingIds));
    }

    public static List<ProgramTraining> createProgramTrainings(Long... trainingIds) {
        return createByIds(id -> createProgramTraining(id, "program training " + id), trainingIds);
    }

    public static LiveData<ProgramTraining> createLiveProgramTraining(long id, String name) {
        ProgramTraining entity = createProgramTraining(id, name);
        return LiveDataUtil.getLive(entity);
    }

    public static ProgramTraining createProgramTraining(long id, String name) {
        ProgramTraining result = new ProgramTraining();
        result.setId(id);
        result.setName(name);
        return result;
    }

    public static LiveData<ProgramExercise> createLiveProgramExercise(long id, long programTrainingId) {
        return LiveDataUtil.getLive(createProgramExercise(id, programTrainingId, 100L));
    }

    public static ProgramExercise createProgramExercise(long id, long programTrainingId, long exerciseId) {
        ProgramExercise entity = new ProgramExercise();
        entity.setId(id);
        entity.setProgramTrainingId(programTrainingId);
        entity.setExerciseId(exerciseId);
        return entity;
    }

    public static LiveData<List<ProgramExercise>> createLiveProgramExercises(int count) {
        return LiveDataUtil.getLive(createProgramExercises(count));
    }

    public static List<ProgramExercise> createProgramExercises(int count) {
        List<ProgramExercise> exercises = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ProgramExercise programExercise = createProgramExercise(i + 2, 1L, i + 100);
            programExercise.setSortOrder(i);
            exercises.add(programExercise);
        }
        return exercises;
    }

    public static LiveData<List<ProgramSet>> createLiveProgramSets(long programExerciseId, int count) {
        return LiveDataUtil.getLive(createProgramSets(programExerciseId, count));
    }

    public static List<ProgramSet> createProgramSets(long programExerciseId, int count) {
        List<ProgramSet> result = Lists.newArrayList();
        for (int i = 0; i < count; i++) {
            ProgramSet programSet = createProgramSet(i + 3, programExerciseId, i + 3);
            programSet.setSortOrder(i);
            result.add(programSet);
        }

        return result;
    }

    public static LiveData<ProgramSet> createLiveProgramSet(long id, long programExerciseId, int reps) {
        return LiveDataUtil.getLive(createProgramSet(id, programExerciseId, reps));
    }

    public static ProgramSet createProgramSet(long id, long programExerciseId, int reps) {
        ProgramSet set = new ProgramSet();
        set.setId(id);
        set.setProgramExerciseId(programExerciseId);
        set.setReps(reps);
        return set;
    }

    public static LiveData<ActualTraining> createLiveActualTraining(long id, long programTrainingId) {
        return LiveDataUtil.getLive(createActualTraining(id, programTrainingId));
    }

    public static ActualTraining createActualTraining(long id, long programTrainingId) {
        ActualTraining training = new ActualTraining(programTrainingId, "foo");
        training.setId(id);
        return training;
    }

    public static ActualExercise createActualExercise(long id, String exerciseName, long actualTrainingId, long programExerciseId) {
        ActualExercise actualExercise = new ActualExercise(exerciseName, actualTrainingId, programExerciseId);
        actualExercise.setId(id);
        return actualExercise;
    }

    public static LiveData<List<ActualExercise>> createLiveActualExercises(Long... ids) {
        return LiveDataUtil.getLive(createActualExercises(ids));
    }

    public static List<ActualExercise> createActualExercises(Long... ids) {
        return createByIds(
                id -> {
                    final int i = Arrays.binarySearch(ids, id) + 1;
                    return createActualExercise(id, "exercise " + i, 10 * i, 1L + i);
                }
                , ids);
    }

    public static LiveData<List<ActualSet>> createLiveActualSets(long actualExerciseId, Long... ids) {
        return LiveDataUtil.getLive(createActualSets(actualExerciseId, ids));
    }

    public static List<ActualSet> createActualSets(long actualExerciseId, Long... ids) {
        return createByIds(id -> createActualSet(id, actualExerciseId, 10), ids);
    }

    public static ActualSet createActualSet(long id, long actualExerciseId, int reps) {
        ActualSet actualSet = new ActualSet(actualExerciseId, reps);
        actualSet.setId(id);
        return actualSet;
    }

    public static LiveData<Exercise> createLiveExercise(long id, String name) {
        return LiveDataUtil.getLive(createExercise(id, name));
    }

    public static Exercise createExercise(long id, String name) {
        Exercise exercise = new Exercise();
        exercise.setId(id);
        exercise.setName(name);
        return exercise;
    }

    public static LiveData<List<Exercise>> createLiveExercises(String... exerciseNames) {
        return LiveDataUtil.getLive(createExercises(exerciseNames));
    }

    public static List<Exercise> createExercises(String... exerciseNames) {
        return createByNames(name -> createExercise(0L, name), 100L, exerciseNames);
    }

    public static LiveData<List<Exercise>> createLiveExercises(int count, long primaryMuscleGroupId) {
        return LiveDataUtil.getLive(createExercises(count, primaryMuscleGroupId));
    }

    public static List<Exercise> createExercises(int count, long primaryMuscleGroupId) {
        List<Exercise> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            long id = 100 + i;
            Exercise exercise = createExercise(id, "exercise" + id);
            exercise.setPrimaryMuscleGroupId(primaryMuscleGroupId);
            result.add(exercise);
        }
        return result;
    }

    public static LiveData<List<MuscleGroup>> createLiveMuscleGroups(Long... muscleGroupIds) {
        return LiveDataUtil.getLive(createMuscleGroups(muscleGroupIds));
    }

    public static List<MuscleGroup> createMuscleGroups(Long... muscleGroupIds) {
        return createByIds(id -> createMuscleGroup(id, "muscle group " + id), muscleGroupIds);
    }

    public static LiveData<MuscleGroup> createLiveMuscleGroup(long id, String name) {
        return LiveDataUtil.getLive(createMuscleGroup(id, name));
    }

    public static MuscleGroup createMuscleGroup(long id, String name) {
        MuscleGroup muscleGroup = new MuscleGroup(name);
        muscleGroup.setId(id);
        return muscleGroup;
    }

    private static <T> List<T> createByIds(Function<Long, T> returnsObject, Long[] ids) {
        List<Long> idsList = Lists.newArrayList(ids);
        return new ArrayList<>(Lists.transform(idsList, returnsObject));
    }

    private static <T extends Model> List<T> createByNames(Function<String, T> returnsObject, long dx, String[] names) {
        List<String> namesList = Lists.newArrayList(names);
        return new ArrayList<>(Lists.transform(namesList, name -> {
            T obj = returnsObject.apply(name);
            obj.setId(namesList.indexOf(name) + dx);
            return obj;
        }));
    }
}
