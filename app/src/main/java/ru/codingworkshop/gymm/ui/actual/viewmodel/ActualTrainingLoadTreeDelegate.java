package ru.codingworkshop.gymm.ui.actual.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import com.google.common.base.Preconditions;

import ru.codingworkshop.gymm.data.tree.loader.ActualTrainingTreeLoader;
import ru.codingworkshop.gymm.data.tree.loader.builder.ActualTrainingTreeBuilder;
import ru.codingworkshop.gymm.data.tree.loader.datasource.ActualTrainingDataSource;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;

/**
 * Created by Радик on 18.09.2017 as part of the Gymm project.
 */
final class ActualTrainingLoadTreeDelegate extends ActualTrainingTreeDelegate {

    public ActualTrainingLoadTreeDelegate(long id) {
        super(id);
    }

    @Override
    LiveData<ActualTrainingTree> load(ActualTrainingTree tree) {
        return Transformations.switchMap(actualTrainingRepository.getActualTrainingById(id), input -> { // FIXME: 26.08.2017 this method invokes two times, should only one
            final Long programTrainingId = Preconditions.checkNotNull(input.getProgramTrainingId());
            return Transformations.switchMap(loadProgramTrainingTree(programTrainingId), unused -> {
                ActualTrainingDataSource dataSource = new ActualTrainingDataSource(actualTrainingRepository, id);
                final ActualTrainingTreeBuilder actualTrainingTreeBuilder = new ActualTrainingTreeBuilder(tree).setProgramTrainingTree(programTrainingTree);
                ActualTrainingTreeLoader loader = new ActualTrainingTreeLoader(actualTrainingTreeBuilder, dataSource);
                return loader.loadIt();
            });
        });
    }
}
