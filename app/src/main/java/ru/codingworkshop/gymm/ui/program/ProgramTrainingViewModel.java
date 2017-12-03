package ru.codingworkshop.gymm.ui.program;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.ChildRestoreAdapter;
import ru.codingworkshop.gymm.data.tree.loader.ProgramTrainingTreeLoader;
import ru.codingworkshop.gymm.data.tree.loader.common.Loader;
import ru.codingworkshop.gymm.data.tree.loader.common.LoaderDelegate;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramTrainingAdapter;
import ru.codingworkshop.gymm.data.tree.saver2.ProgramTrainingTreeSaver;
import ru.codingworkshop.gymm.data.tree.saver2.Saver;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.db.GymmDatabase;

/**
 * Created by Radik on 28.11.2017.
 */

public class ProgramTrainingViewModel extends ViewModel {
    private final Loader<ProgramTrainingTree> loader;
    private final Saver<ProgramTrainingTree> saver;
    private final ProgramTrainingAdapter repositoryAdapter;

    private final ProgramTrainingTree tree;
    private final MutableLiveData<ProgramExerciseNode> currentProgramExercise;
    private final MutableLiveData<ProgramSet> currentProgramSet;

    @Inject
    public ProgramTrainingViewModel(ProgramTrainingTreeLoader loader, ProgramTrainingTreeSaver saver, ProgramTrainingAdapter repositoryAdapter) {
        this.loader = loader;
        this.saver = saver;
        this.repositoryAdapter = repositoryAdapter;

        tree = new MutableProgramTrainingTree();
        tree.setParent(new ProgramTraining());

        currentProgramExercise = new MutableLiveData<>();
        currentProgramSet = new MutableLiveData<>();
    }

    public @NonNull LiveData<ProgramTrainingTree> loadTree(long programTrainingId) {
        return loader.loadById(tree, programTrainingId);
    }

    public void saveTree() {
        saver.save(tree);
    }

    public @NonNull ProgramTrainingTree getProgramTrainingTree() {
        return tree;
    }

    public @NonNull LiveData<Boolean> validateProgramTraining() {
        return Transformations.map(
                repositoryAdapter.getProgramTrainingByName(tree.getParent().getName()),
                programTraining -> programTraining == null || programTraining.getId() == tree.getParent().getId());
    }

    /**
     * creates {@link ProgramExerciseNode} and attaches it to tree
     * @return index
     */
    public int createProgramExercise() {
        ProgramExercise programExercise = new ProgramExercise();
        programExercise.setProgramTrainingId(tree.getParent().getId());

        ProgramExerciseNode programExerciseNode = tree.createChildNode(programExercise);
        tree.addChild(programExerciseNode);

        setProgramExercise(programExerciseNode);

        return programExercise.getSortOrder();
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
        currentProgramExercise.setValue(programExercise);
    }

    @NonNull public LiveData<ProgramExerciseNode> getProgramExercise() {
        return currentProgramExercise;
    }

    public LiveData<Boolean> isProgramTrainingChanged() {
        ProgramTraining newParent = tree.getParent();
        if (!GymmDatabase.isValidId(newParent)) {
            return LiveDataUtil.getLive(!Strings.isNullOrEmpty(newParent.getName()));
        }
        return Transformations.map(
                repositoryAdapter.getParent(newParent.getId()),
                oldParent -> !Objects.equal(newParent.getName(), oldParent.getName()));
    }

    public LiveData<Boolean> areProgramExercisesChanged() {
        return Transformations.map(
                repositoryAdapter.getChildren(tree.getParent().getId()),
                exercises -> !Objects.equal(tree.getProgramExercises(), exercises));
    }

    /**
     * creates {@link ProgramSet} instance and adds it in tree
     * @return index
     */
    public int createProgramSet() {
        ProgramExerciseNode currentExercise = getCurrentExerciseNode();

        ProgramSet programSet = new ProgramSet();
        programSet.setProgramExerciseId(currentExercise.getId());
        programSet.setReps(1); // default value

        currentExercise.addChild(programSet);

        setProgramSet(programSet);

        return programSet.getSortOrder();
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
        return Preconditions.checkNotNull(currentProgramExercise.getValue(), "create or select exercise");
    }

    public void setProgramSet(@Nullable ProgramSet programSet) {
        currentProgramSet.setValue(programSet);
    }

    @NonNull public LiveData<ProgramSet> getProgramSet() {
        return currentProgramSet;
    }

    public LiveData<Boolean> areProgramSetsChanged() {
        ProgramExerciseNode exerciseNode = Preconditions.checkNotNull(getProgramExercise().getValue(), "program exercise has not been set");
        return Transformations.map(
                repositoryAdapter.getProgramSetsForExercise(exerciseNode.getId()),
                sets -> !exerciseNode.getChildren().equals(sets));

    }
}
