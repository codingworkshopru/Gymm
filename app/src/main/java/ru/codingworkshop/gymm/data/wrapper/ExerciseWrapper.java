package ru.codingworkshop.gymm.data.wrapper;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.db.dao.ExerciseDao;
import ru.codingworkshop.gymm.db.dao.MuscleGroupDao;

/**
 * Created by Радик on 19.06.2017.
 */

public class ExerciseWrapper {
    private Exercise exercise;
    private Set<MuscleGroup> secondaryMuscleGroups = Sets.newHashSet();

    public ExerciseWrapper() {

    }

    public ExerciseWrapper(@NonNull Exercise exercise) {
        this.exercise = exercise;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public void addSecondaryMuscleGroup(MuscleGroup muscleGroup) {
        Preconditions.checkArgument(muscleGroup != null, "Muscle group cannot be null");
        secondaryMuscleGroups.add(muscleGroup);
    }

    public void setSecondaryMuscleGroups(Collection<? extends MuscleGroup> secondaryMuscleGroups) {
        this.secondaryMuscleGroups = new HashSet<>(secondaryMuscleGroups);
    }

    public Set<MuscleGroup> getSecondaryMuscleGroups() {
        return Collections.unmodifiableSet(secondaryMuscleGroups);
    }

    public int getSecondaryMuscleGroupsCount() {
        return secondaryMuscleGroups.size();
    }

    public boolean hasSecondaryMuscleGroups() {
        return !secondaryMuscleGroups.isEmpty();
    }

    public void removeSecondaryMuscleGroup(MuscleGroup muscleGroup) {
        Preconditions.checkArgument(muscleGroup != null, "Muscle group cannot be null");
        secondaryMuscleGroups.remove(muscleGroup);
    }

    public static LiveData<ExerciseWrapper> load(long id, ExerciseDao exerciseDao, MuscleGroupDao muscleGroupDao) {
        Loader<ExerciseWrapper> loader = new Loader<>(ExerciseWrapper::new);
        loader.addSource(exerciseDao.getExerciseById(id), ExerciseWrapper::setExercise);
        loader.addSource(muscleGroupDao.getSecondaryMuscleGroupsForExercise(id), ExerciseWrapper::setSecondaryMuscleGroups);
        return loader.load();
    }

    public static LiveData<ExerciseWrapper> create() {
        MutableLiveData<ExerciseWrapper> liveWrapper = new MutableLiveData<>();
        Exercise exercise = new Exercise();
        ExerciseWrapper wrapper = new ExerciseWrapper(exercise);
        liveWrapper.setValue(wrapper);
        return liveWrapper;
    }
}
