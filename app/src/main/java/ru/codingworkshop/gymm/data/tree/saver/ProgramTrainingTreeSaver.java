package ru.codingworkshop.gymm.data.tree.saver;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import ru.codingworkshop.gymm.data.entity.IProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ChildrenAdapter;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramTrainingAdapter;
import ru.codingworkshop.gymm.db.GymmDatabase;

/**
 * Created by Radik on 29.11.2017.
 */

public class ProgramTrainingTreeSaver implements Saver<ProgramTrainingTree> {
    private ProgramTrainingAdapter adapter;

    @Inject
    ProgramTrainingTreeSaver(ProgramTrainingAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void save(@NonNull ProgramTrainingTree tree) {
        ProgramTraining parent = tree.getParent();

        List<ProgramExerciseNode> programExerciseNodes = tree.getChildren();

        if (!GymmDatabase.isValidId(parent)) {
            Completable
                    .fromRunnable(() -> {
                        long parentId = adapter.insertParent(parent);

                        setParentIdToEveryExercise(programExerciseNodes, parentId);
                        List<Long> ids = adapter.insertChildren(tree.getProgramExercises());

                        setParentIdToEverySet(programExerciseNodes, ids);
                        adapter.insertGrandchildren(tree.getAllProgramSets());
                    })
                    .subscribeOn(Schedulers.single())
                    .subscribe();
        } else {
            long parentId = parent.getId();
            adapter.getChildren(parentId)
                    .take(1)
                    .subscribe(oldChildren -> {
                        adapter.updateParent(tree.getParent());

                        ChildrenSaver.ContainersDiffResult<ProgramExercise> diffResult =
                                ChildrenSaver.containersDiff(oldChildren, tree.getProgramExercises());

                        adapter.deleteChildren(diffResult.getToDelete());
                        adapter.updateChildren(diffResult.getToUpdate());

                        Collection<ProgramExercise> toInsert = diffResult.getToInsert();
                        List<Long> ids = adapter.insertChildren(toInsert);
                        setParentIdToEverySet(Collections2.filter(tree.getChildren(), ch -> toInsert.contains(ch.getParent())), ids);

                        new ChildrenSaver<>(new ChildrenAdapter<ProgramSet>() {
                            @Override
                            public Flowable<List<ProgramSet>> getChildren(long id) {
                                return adapter.getGrandchildren(id);
                            }

                            @Override
                            public List<Long> insertChildren(Collection<ProgramSet> children) {
                                adapter.insertGrandchildren(children);
                                return null;
                            }

                            @Override
                            public void updateChildren(Collection<ProgramSet> children) {
                                adapter.updateGrandchildren(children);
                            }

                            @Override
                            public void deleteChildren(Collection<ProgramSet> children) {
                                adapter.deleteGrandchildren(children);
                            }
                        }, parentId).save(tree.getAllProgramSets());
                    });
        }
    }

    private void setParentIdToEverySet(Collection<ProgramExerciseNode> programExerciseNodes, Collection<Long> childrenIds) {
        Preconditions.checkArgument(programExerciseNodes.size() == childrenIds.size(), "collection exercises and their ids must have the same size");

        Iterator<ProgramExerciseNode> nodeIt = programExerciseNodes.iterator();
        Iterator<Long> idIt = childrenIds.iterator();

        while (nodeIt.hasNext() && idIt.hasNext()) {
            ProgramExerciseNode n = nodeIt.next();
            long id = idIt.next();

            for (ProgramSet s : n.getChildren()) {
                s.setProgramExerciseId(id);
            }
        }
    }

    private void setParentIdToEveryExercise(List<? extends IProgramExercise> programExercises, long parentId) {
        for (IProgramExercise n : programExercises) {
            n.setProgramTrainingId(parentId);
        }
    }
}
