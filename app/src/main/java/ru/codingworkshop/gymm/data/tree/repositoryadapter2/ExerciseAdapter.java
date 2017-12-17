package ru.codingworkshop.gymm.data.tree.repositoryadapter2;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Single;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.db.dao.ExerciseDao;
import ru.codingworkshop.gymm.db.dao.MuscleGroupDao;

/**
 * Created by Radik on 17.12.2017.
 */

public class ExerciseAdapter implements ParentAdapter<Exercise>, ChildrenAdapter<MuscleGroup> {
    private ExerciseDao exerciseDao;
    private MuscleGroupDao muscleGroupDao;

    @Inject
    ExerciseAdapter(ExerciseDao exerciseDao, MuscleGroupDao muscleGroupDao) {
        this.exerciseDao = exerciseDao;
        this.muscleGroupDao = muscleGroupDao;
    }

    public Single<MuscleGroup> getPrimaryMuscleGroup(long exerciseId) {
        return muscleGroupDao.getPrimaryMuscleGroupForExerciseRx(exerciseId);
    }

    @Override
    public Single<Exercise> getParent(long parentId) {
        return exerciseDao.getExerciseByIdRx(parentId);
    }

    @Override
    public long insertParent(Exercise parent) {
        return 0L;
    }

    @Override
    public void updateParent(Exercise parent) {

    }

    @Override
    public void deleteParent(Exercise parent) {

    }

    @Override
    public Maybe<List<MuscleGroup>> getChildren(long parentId) {
        return muscleGroupDao.getSecondaryMuscleGroupsForExerciseRx(parentId);
    }

    @Override
    public List<Long> insertChildren(Collection<MuscleGroup> children) {
        return null;
    }

    @Override
    public void updateChildren(Collection<MuscleGroup> children) {

    }

    @Override
    public void deleteChildren(Collection<MuscleGroup> children) {

    }
}
