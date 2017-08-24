package ru.codingworkshop.gymm.data.tree.loader;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.tree.loader.datasource.ProgramExerciseDataSource;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;

/**
 * Created by Радик on 14.08.2017 as part of the Gymm project.
 */
public final class ProgramExerciseLoader extends NodeLoader<ProgramExercise, ProgramSet> {
    private ProgramExerciseDataSource dataSource;

    public ProgramExerciseLoader(@NonNull ProgramExerciseNode node, @NonNull ProgramExerciseDataSource dataSource) {
        super(node, dataSource);
        this.dataSource = Preconditions.checkNotNull(dataSource);
    }

    @Override
    void loadAdditional(SetAndRemove setAndRemove) {
        ProgramExerciseNode node = (ProgramExerciseNode) getNode();
        setAndRemove.ok(dataSource.getExercise(), node::setExercise);
    }
}
