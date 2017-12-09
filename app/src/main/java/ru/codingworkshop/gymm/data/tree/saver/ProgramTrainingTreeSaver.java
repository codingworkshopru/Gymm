package ru.codingworkshop.gymm.data.tree.saver;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.entity.IProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ChildrenAdapter;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramTrainingAdapter;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.db.GymmDatabase;

/**
 * Created by Radik on 29.11.2017.
 */

public class ProgramTrainingTreeSaver implements Saver<ProgramTrainingTree> {
    private ProgramTrainingAdapter adapter;

    @Inject
    public ProgramTrainingTreeSaver(ProgramTrainingAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void save(@NonNull ProgramTrainingTree tree) { // TODO refactor this as soon as possible
        ProgramTraining parent = tree.getParent();

        List<ProgramExerciseNode> programExerciseNodes = tree.getChildren();

        if (!GymmDatabase.isValidId(parent)) {
            LiveDataUtil.getOnce(adapter.insertParent(parent), parentId -> {
                setParentIdToEveryExercise(programExerciseNodes, parentId);
                LiveDataUtil.getOnce(adapter.insertChildren(tree.getProgramExercises()), childrenIds -> {
                    setParentIdToEverySet(programExerciseNodes, childrenIds);
                    adapter.insertGrandchildren(tree.getAllProgramSets());
                });
            });
        } else {
            adapter.updateParent(parent);
            final MutableLiveData<Boolean> exercisesInserted = new MutableLiveData<>();
            ChildrenAdapter<ProgramExercise> exercisesAdapter = new ChildrenAdapter<ProgramExercise>() {
                @Override
                public LiveData<List<ProgramExercise>> getChildren(long id) {
                    return adapter.getChildren(id);
                }

                @Override
                public LiveData<List<Long>> insertChildren(Collection<ProgramExercise> children) {
                    setParentIdToEveryExercise(children, parent.getId());
                    return LiveDataUtil.getOnce(adapter.insertChildren(children), childrenIds -> {
                        setParentIdToEverySet(programExerciseNodes, childrenIds);
                        exercisesInserted.setValue(true);
                    });
                }

                @Override
                public void updateChildren(Collection<ProgramExercise> children) {
                    adapter.updateChildren(children);
                }

                @Override
                public void deleteChildren(Collection<ProgramExercise> children) {
                    adapter.deleteChildren(children);
                }
            };
            new ChildrenSaver<>(exercisesAdapter, parent.getId()).save(tree.getProgramExercises());
            LiveDataUtil.getOnce(exercisesInserted, i -> {
                if (i != null && i) {
                    new ChildrenSaver<>(new ChildrenAdapter<ProgramSet>() {
                        @Override
                        public LiveData<List<ProgramSet>> getChildren(long id) {
                            return adapter.getGrandchildren(id);
                        }

                        @Override
                        public LiveData<List<Long>> insertChildren(Collection<ProgramSet> children) {
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
                    }, parent.getId())
                            .save(Lists.newArrayList(tree.getAllProgramSets()));
                }
            });
        }
    }

    private void setParentIdToEverySet(List<ProgramExerciseNode> programExerciseNodes, List<Long> childrenIds) {
        for (int i = 0; i < childrenIds.size(); i++) {
            ProgramExerciseNode n = programExerciseNodes.get(i);
            long id = childrenIds.get(i);

            for (ProgramSet s : n.getChildren()) {
                s.setProgramExerciseId(id);
            }
        }
    }

    private void setParentIdToEveryExercise(Collection<? extends IProgramExercise> programExerciseNodes, Long parentId) {
        for (IProgramExercise n : programExerciseNodes) {
            n.setProgramTrainingId(parentId);
        }
    }
}
