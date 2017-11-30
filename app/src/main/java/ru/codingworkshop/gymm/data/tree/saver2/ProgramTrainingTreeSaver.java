package ru.codingworkshop.gymm.data.tree.saver2;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ChildrenAdapter;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramTrainingAdapter;

/**
 * Created by Radik on 29.11.2017.
 */

public class ProgramTrainingTreeSaver implements Saver<ProgramTrainingTree> {
    private ProgramTrainingAdapter adapter;
    private final ModelSaver<ProgramTraining> modelSaver;

    @Inject
    public ProgramTrainingTreeSaver(ProgramTrainingAdapter adapter) {
        this.adapter = adapter;
        modelSaver = new ModelSaver<>(adapter);
    }

    @Override
    public void save(@NonNull ProgramTrainingTree tree) {
        ProgramTraining programTraining = tree.getParent();
        modelSaver.save(programTraining);

        long programTrainingId = programTraining.getId();
        new ChildrenSaver<>(adapter, programTrainingId, Objects::equal)
                .save(Lists.newArrayList(tree.childrenIterator()));

        new ChildrenSaver<>(new ChildrenAdapter<ProgramSet>() {
            @Override
            public LiveData<List<ProgramSet>> getChildren(long id) {
                return adapter.getGrandchildren(id);
            }

            @Override
            public void insertChildren(Collection<ProgramSet> children) {
                adapter.insertGrandchildren(children);
            }

            @Override
            public void updateChildren(Collection<ProgramSet> children) {
                adapter.updateGrandchildren(children);
            }

            @Override
            public void deleteChildren(Collection<ProgramSet> children) {
                adapter.deleteGrandchildren(children);
            }
        }, programTrainingId, Objects::equal)
        .save(Lists.newArrayList(tree.grandchildrenIterator()));
    }
}
