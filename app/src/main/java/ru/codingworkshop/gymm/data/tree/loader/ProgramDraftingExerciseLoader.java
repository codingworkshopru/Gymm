package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramExerciseAdapter;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;

/**
 * Created by Radik on 17.11.2017.
 */

public class ProgramDraftingExerciseLoader extends ProgramExerciseLoader {
    private ProgramExerciseAdapter dataSource;

    @Inject
    public ProgramDraftingExerciseLoader(@NonNull ProgramExerciseAdapter dataSource) {
        super(dataSource);
        this.dataSource = dataSource;
    }

    @Override
    public LiveData<ProgramExerciseNode> loadById(@NonNull ProgramExerciseNode node, long id) {
        return Transformations.switchMap(dataSource.getDrafting(id), programExercise -> {
            return programExercise != null
                    ? ProgramDraftingExerciseLoader.super.loadById(node, programExercise.getId())
                    : LiveDataUtil.getAbsent();
        });
    }
}
