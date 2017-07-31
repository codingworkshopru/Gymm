package ru.codingworkshop.gymm.util;

import android.arch.lifecycle.LiveData;

import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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

public class ModelsFixture {
    public static List<SimpleModel> createSimpleModels(String... names) {
        List<String> namesList = Lists.newArrayList(names);
        return namesList.stream().map(
                name -> new SimpleModel(namesList.contains(name) ? namesList.indexOf(name) + 1L : -1L, name)
        ).collect(Collectors.toList());
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

    public static LiveData<ProgramTraining> createLiveProgramTraining(long id, String name, boolean drafting) {
        ProgramTraining entity = createProgramTraining(id, name);
        entity.setDrafting(drafting);
        return LiveDataUtil.getLive(entity);
    }

    public static ProgramTraining createProgramTraining(long id, String name) {
        ProgramTraining result = new ProgramTraining();
        result.setId(id);
        result.setName(name);
        return result;
    }

    public static LiveData<ProgramExercise> createLiveProgramExercise(long id, long programTrainingId, boolean drafting) {
        return LiveDataUtil.getLive(createProgramExercise(id, programTrainingId, 0, drafting));
    }

    public static ProgramExercise createProgramExercise(long id, long programTrainingId, long exerciseId, boolean drafting) {
        ProgramExercise entity = new ProgramExercise();
        entity.setId(id);
        entity.setProgramTrainingId(programTrainingId);
        entity.setDrafting(drafting);
        entity.setExerciseId(exerciseId);
        return entity;
    }

    public static List<ProgramExercise> createProgramExercises(int count) {
        List<ProgramExercise> exercises = Lists.newArrayListWithCapacity(count);
        for (int i = 0; i < count; i++) {
            ProgramExercise programExercise = createProgramExercise(i + 2, 1L, i, false);
            programExercise.setSortOrder(i);
            exercises.add(programExercise);
        }
        return exercises;
    }

    public static ProgramSet createProgramSet(long id, long programExerciseId, int reps) {
        ProgramSet set = new ProgramSet();
        set.setId(id);
        set.setProgramExerciseId(programExerciseId);
        set.setReps(reps);
        return set;
    }

    public static LiveData<ProgramSet> createLiveProgramSet(long id, long programExerciseId, int reps) {

        return LiveDataUtil.getLive(createProgramSet(id, programExerciseId, reps));
    }

    public static List<ProgramSet> createProgramSets(int count) {
        List<ProgramSet> result = Lists.newArrayList();
        for (int i = 0; i < count; i++) {
            ProgramSet programSet = createProgramSet(i + 3, 2L, i * 10);
            programSet.setSortOrder(i);
            result.add(programSet);
        }

        return result;
    }

    public static LiveData<List<ProgramSet>> createLiveProgramSets(int count) {
        return LiveDataUtil.getLive(createProgramSets(count));
    }

    public static Exercise createExercise(long id, String name) {
        Exercise exercise = new Exercise();
        exercise.setId(id);
        exercise.setName(name);
        return exercise;
    }

    public static ActualTraining createActualTraining(long id, long programTrainingId) {
        ActualTraining training = new ActualTraining();
        training.setId(id);
        training.setProgramTrainingId(programTrainingId);
        training.setStartTime(new Date());
        return training;
    }

    public static LiveData<Exercise> createLiveExercise(long id, String name) {
        return LiveDataUtil.getLive(createExercise(id, name));
    }

    public static List<Exercise> createExercises(String... exerciseNames) {
        return createByNames(name -> createExercise(0L, name), 100L, exerciseNames);
    }

    public static LiveData<List<MuscleGroup>> createLiveMuscleGroups(Long... muscleGroupIds) {
        return LiveDataUtil.getLive(createMuscleGroups(muscleGroupIds));
    }

    public static List<MuscleGroup> createMuscleGroups(Long... muscleGroupIds) {
        return createByIds(id -> createMuscleGroup(id, "muscle group " + id), muscleGroupIds);
    }

    public static MuscleGroup createMuscleGroup(long id, String name) {
        MuscleGroup muscleGroup = new MuscleGroup(name);
        muscleGroup.setId(id);
        return muscleGroup;
    }

    private static <T> List<T> createByIds(Function<Long, T> returnsObject, Long... ids) {
        List<Long> idsList = Lists.newArrayList(ids);
        return idsList.stream().map(returnsObject).collect(Collectors.toList());
    }

    private static <T extends Model> List<T> createByNames(Function<String, T> returnsObject, long dx, String... names) {
        List<String> namesList = Lists.newArrayList(names);
        return namesList.stream().map(name -> {
            T obj = returnsObject.apply(name);
            obj.setId(namesList.indexOf(name) + dx);
            return obj;
        }).collect(Collectors.toList());
    }
}
