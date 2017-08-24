package ru.codingworkshop.gymm.data.tree.loader;

import android.support.annotation.NonNull;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.loader.adapter.ProgramTrainingTreeAdapter;
import ru.codingworkshop.gymm.data.tree.loader.datasource.ProgramTrainingDataSource;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;

/**
 * Created by Радик on 16.08.2017 as part of the Gymm project.
 */

public class ProgramTrainingTreeLoader extends TreeLoader<ProgramTraining, ProgramExercise, ProgramSet> {
    private ProgramTrainingDataSource dataSource;

    public ProgramTrainingTreeLoader(@NonNull ProgramTrainingTree tree, @NonNull ProgramTrainingDataSource dataSource) {
        super(new ProgramTrainingTreeAdapter(tree), dataSource);
        this.dataSource = dataSource;
    }

    @Override
    void loadAdditional(SetAndRemove setAndRemove) {
        super.loadAdditional(setAndRemove);
        ProgramTrainingTreeAdapter adapter = (ProgramTrainingTreeAdapter) getNode();
        setAndRemove.ok(dataSource.getExercises(), adapter::setExercises);
    }
}