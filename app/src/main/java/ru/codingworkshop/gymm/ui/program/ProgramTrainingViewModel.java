package ru.codingworkshop.gymm.ui.program;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.ChildRestoreAdapter;
import ru.codingworkshop.gymm.data.tree.loader.ProgramTrainingTreeLoader;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramTrainingAdapter;
import ru.codingworkshop.gymm.data.tree.saver.ProgramTrainingTreeSaver;
import ru.codingworkshop.gymm.data.tree.saver.Saver;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.data.util.LoaderAdapter;
import ru.codingworkshop.gymm.db.GymmDatabase;

/**
 * Created by Radik on 28.11.2017.
 */

public class ProgramTrainingViewModel extends ViewModel {
    private final LoaderAdapter<ProgramTrainingTree> loaderAdapter;
    private final Saver<ProgramTrainingTree> saver;
    private final ProgramTrainingAdapter repositoryAdapter;

    private final ProgramTrainingTree tree;
    private final MutableLiveData<ProgramExerciseNode> currentProgramExercise;
    private final MutableLiveData<ProgramSet> currentProgramSet;

    @Inject
    public ProgramTrainingViewModel(ProgramTrainingTreeLoader loader, ProgramTrainingTreeSaver saver, ProgramTrainingAdapter repositoryAdapter) {
        this.saver = saver;
        this.repositoryAdapter = repositoryAdapter;

        tree = new MutableProgramTrainingTree();
        ProgramTraining training = new ProgramTraining();
        tree.setParent(training);

        loaderAdapter = new LoaderAdapter<>(loader, tree);
        currentProgramExercise = new MutableLiveData<>();
        currentProgramSet = new MutableLiveData<>();
    }

    @NonNull
    public LiveData<ProgramTrainingTree> loadTree(long programTrainingId) {
        return loaderAdapter.load(programTrainingId);
    }

    public void saveTree() {
        Completable save = saver.save(tree);
        if (save != null) {
            save.subscribeOn(Schedulers.io()).subscribe();
        }
    }

    @NonNull public ProgramTrainingTree getProgramTrainingTree() {
        return tree;
    }

    @NonNull public LiveData<Boolean> validateProgramTraining() {
        return Transformations.map(
                repositoryAdapter.getProgramTrainingByName(tree.getParent().getName()),
                programTraining -> programTraining == null || programTraining.getId() == tree.getParent().getId());
    }

    public LiveData<Boolean> isProgramTrainingChanged() {
        return LiveDataReactiveStreams.fromPublisher(
                Flowable.just(tree.getParent())
                        .map(newParent -> {
                            if (!GymmDatabase.isValidId(newParent)) {
                                boolean nameChanged = !Strings.isNullOrEmpty(newParent.getName());
                                boolean childrenChanged = tree.hasChildren();
                                return nameChanged || childrenChanged;
                            } else {
                                ProgramTraining persistedParent = repositoryAdapter.getParent(newParent.getId());
                                boolean hasSameNames = persistedParent.getName().equals(newParent.getName());
                                if (!hasSameNames) {
                                    return true;
                                } else {
                                    return !Objects.equal(repositoryAdapter.getChildren(persistedParent.getId()), tree.getProgramExercises());
                                }
                            }
                        })
                        .subscribeOn(Schedulers.io())
        );
    }

    /**
     * creates {@link ProgramExerciseNode} and attaches it to tree
     */
    public void createProgramExercise() {
        ProgramExercise programExercise = new ProgramExercise();
        programExercise.setProgramTrainingId(tree.getParent().getId());
        programExercise.setSortOrder(-1);

        ProgramExerciseNode programExerciseNode = tree.createChildNode(programExercise);

        currentProgramExercise.setValue(programExerciseNode);
    }

    public void saveProgramExercise() {
        ProgramExerciseNode current = getCurrentExerciseNode();
        int sortOrder = current.getSortOrder();
        if (sortOrder == -1) {
            tree.addChild(current);
        } else {
            tree.replaceChild(sortOrder, current);
        }
        currentProgramExercise.setValue(null);
    }

    public void deleteProgramExercise(ProgramExerciseNode programExerciseNode) {
        tree.removeChild(programExerciseNode);
    }

    public void deleteProgramExercise(ProgramExercise programExercise) {
        deleteProgramExercise(programExercise.getSortOrder());
    }

    public void deleteProgramExercise(int programExerciseIndex) {
        tree.removeChild(programExerciseIndex);
    }

    public void restoreLastDeletedProgramExercise() {
        new ChildRestoreAdapter(tree).restoreLastRemoved();
    }

    public void moveProgramExercise(int from, int to) {
        tree.moveChild(from, to);
    }

    public void setProgramExercise(@Nullable ProgramExerciseNode programExercise) {
        if (programExercise != null) {
            programExercise = new MutableProgramExerciseNode(programExercise);
        }
        currentProgramExercise.setValue(programExercise);
    }

    @NonNull public LiveData<ProgramExerciseNode> getProgramExercise() {
        return currentProgramExercise;
    }

    public boolean isProgramExerciseChanged() {
        ProgramExerciseNode newNode = getCurrentExerciseNode();

        Long exerciseId = newNode.getExerciseId();
        if (newNode.getSortOrder() == -1) {
            return (exerciseId != null && GymmDatabase.isValidId(exerciseId)) || newNode.hasChildren();
        }

        ProgramExerciseNode oldNode = tree.getChildren().get(newNode.getSortOrder());
        return !Objects.equal(oldNode.getExerciseId(), exerciseId) || !oldNode.getChildren().equals(newNode.getChildren());
    }

    /**
     * creates {@link ProgramSet} instance and adds it in tree
     */
    public void createProgramSet() {
        ProgramExerciseNode currentExercise = getCurrentExerciseNode();

        ProgramSet programSet = new ProgramSet();
        programSet.setProgramExerciseId(currentExercise.getId());
        programSet.setReps(1); // default value
        programSet.setSortOrder(-1);

        setProgramSet(programSet);
    }

    public void saveProgramSet() {
        ProgramExerciseNode currentExercise = getCurrentExerciseNode();
        ProgramSet currentSet = Preconditions.checkNotNull(currentProgramSet.getValue(), "current set is not set");
        int sortOrder = currentSet.getSortOrder();
        if (sortOrder == -1) {
            currentExercise.addChild(currentSet);
        } else {
            currentExercise.replaceChild(sortOrder, currentSet);
        }
        currentProgramSet.setValue(null);
    }

    public void deleteProgramSet(@NonNull ProgramSet programSet) {
        getCurrentExerciseNode().removeChild(programSet);
    }

    public void deleteProgramSet(int programSetIndex) {
        getCurrentExerciseNode().removeChild(programSetIndex);
    }

    public void restoreLastDeletedProgramSet() {
        new ChildRestoreAdapter(getCurrentExerciseNode()).restoreLastRemoved();
    }

    public void moveProgramSet(int from, int to) {
        getCurrentExerciseNode().moveChild(from, to);
    }

    private ProgramExerciseNode getCurrentExerciseNode() {
        return Preconditions.checkNotNull(currentProgramExercise.getValue(), "you must create or set exercise");
    }

    public void setProgramSet(@Nullable ProgramSet programSet) {
        if (programSet != null) {
            programSet = new ProgramSet(programSet);
        }
        currentProgramSet.setValue(programSet);
    }

    @NonNull public LiveData<ProgramSet> getProgramSet() {
        return currentProgramSet;
    }

    @Override
    protected void onCleared() {
        loaderAdapter.clear();
    }
}
