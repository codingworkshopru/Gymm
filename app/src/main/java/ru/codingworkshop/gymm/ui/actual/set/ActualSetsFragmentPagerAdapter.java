package ru.codingworkshop.gymm.ui.actual.set;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.tree.node.ActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.databinding.FragmentActualSetBinding;
import timber.log.Timber;

/**
 * Created by Радик on 16.09.2017 as part of the Gymm project.
 */
public class ActualSetsFragmentPagerAdapter extends FragmentPagerAdapter {
    private ActualExerciseNode actualExerciseNode;
    private SparseArray<ActualSetFragment> fragments = new SparseArray<>();
    private int childrenCount;

    public ActualSetsFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        Timber.d("Constructor call");
    }

    @Override
    public Fragment getItem(int position) {
        Timber.d("getItem: %d", position);
        Fragment fragment = getOrCreateFragment(position);
        bindData(position);
        return fragment;
    }

    private ActualSetFragment getOrCreateFragment(int position) {
        ActualSetFragment actualSetFragment = fragments.get(position, null);
        if (actualSetFragment == null) {
            actualSetFragment = ActualSetFragment.newInstance();
            fragments.append(position, actualSetFragment);
        }

        return actualSetFragment;
    }

    @Override
    public int getCount() {
        return childrenCount;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        Timber.d("destroyItem: %d", position);
    }

    @Override
    public int getItemPosition(Object object) {
        final int index = fragments.indexOfValue((ActualSetFragment) object);
        return index == -1 || index >= getCount() ? POSITION_NONE : POSITION_UNCHANGED;
    }

    @Nullable
    public FragmentActualSetBinding getBinding(int index) {
        return index < fragments.size() ? fragments.get(index).getBinding() : null;
    }

    public void setActualExerciseNode(ActualExerciseNode exerciseNode) {
        actualExerciseNode = exerciseNode;
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        findCount();

        for (int i = 0; i < fragments.size(); i++) {
            bindData(i);
        }

        super.notifyDataSetChanged();
    }

    private void bindData(int index) {
        Timber.d("bindData; index: %d", index);
        final ProgramExerciseNode programExerciseNode = actualExerciseNode.getProgramExerciseNode();
        final List<ProgramSet> programSets = programExerciseNode.getChildren();
        final List<ActualSet> actualSets = actualExerciseNode.getChildren();

        final ActualSet actualSet = Iterables.get(actualSets, index,
                new ActualSet(actualExerciseNode.getParent().getId(), 0));

        final ProgramSet programSet = Iterables.get(programSets, index, null);

        Bundle arguments = new ActualSetFragment.ArgumentsBuilder()
                .setActualSet(actualSet)
                .setSetIndex(index)
                .setSetsCount(programSets.size()) // TODO increase dynamically if sets count more that planned
                .setProgramSet(programSet)
                .setWithWeight(programExerciseNode.getExercise().isWithWeight())
                .build();

        ActualSetFragment foundFragment = getOrCreateFragment(index);
        foundFragment.setArguments(arguments);
    }

    private void findCount() {
        int programSetsCount = actualExerciseNode.getProgramExerciseNode().getChildren().size();
        int actualSetsCount = actualExerciseNode.getChildren().size();

        childrenCount = actualSetsCount + 1;
        if (childrenCount > programSetsCount) {
            childrenCount = programSetsCount;
        }

        Timber.d("findCount; childrenCount: %d; fragments.size(): %d", childrenCount, fragments.size());
    }
}
