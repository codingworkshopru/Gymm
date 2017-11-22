package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramTrainingAdapter;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;

/**
 * Created by Radik on 17.11.2017.
 */

public class ProgramDraftingTrainingTreeLoader extends ProgramTrainingTreeLoader {
    private ProgramTrainingAdapter dataSource;

    @Inject
    public ProgramDraftingTrainingTreeLoader(@NonNull ProgramTrainingAdapter dataSource) {
        super(dataSource);
        this.dataSource = dataSource;
    }

    @Override
    public LiveData<ProgramTrainingTree> load(ProgramTrainingTree tree) {
        return Transformations.switchMap(dataSource.getDrafting(), programTraining -> {
            return programTraining != null
                    ? ProgramDraftingTrainingTreeLoader.super.loadById(tree, programTraining.getId())
                    : LiveDataUtil.getAbsent();
        });
    }
}
