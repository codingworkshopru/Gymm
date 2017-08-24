package ru.codingworkshop.gymm.data.tree.loader;

import android.support.annotation.NonNull;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.data.tree.loader.datasource.ExerciseDataSource;
import ru.codingworkshop.gymm.data.tree.node.ExerciseNode;

/**
 * Created by Радик on 18.08.2017 as part of the Gymm project.
 */

public class ExerciseLoader extends NodeLoader<Exercise, MuscleGroup> {
    private ExerciseDataSource dataSource;

    public ExerciseLoader(@NonNull ExerciseNode node, @NonNull ExerciseDataSource dataSource) {
        super(node, dataSource);
        this.dataSource = dataSource;
    }

    @Override
    void loadAdditional(SetAndRemove setAndRemove) {
        setAndRemove.ok(dataSource.getPrimaryMuscleGroup(), muscleGroup -> {
            final ExerciseNode node = (ExerciseNode) getNode();
            node.setPrimaryMuscleGroup(muscleGroup);
        });
    }
}
