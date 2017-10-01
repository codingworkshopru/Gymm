package ru.codingworkshop.gymm.ui.actual;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.tree.node.ActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.databinding.FragmentActualSetBinding;

/**
 * Created by Радик on 16.09.2017 as part of the Gymm project.
 */
public class ActualSetsFragmentPagerAdapter extends FragmentPagerAdapter {
    private ActualExerciseNode actualExerciseNode;
    private List<ActualSetFragment> fragments;
    private int childrenCount;

    public ActualSetsFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        final ActualSetFragment actualSetFragment = ActualSetFragment.newInstance();
        if (position == fragments.size()) {
            fragments.add(actualSetFragment);
        } else {
            fragments.set(position, actualSetFragment);
        }
        bindData(position);
        return actualSetFragment;
    }

    @Override
    public int getCount() {
        return childrenCount;
    }


    @Nullable
    public FragmentActualSetBinding getBinding(int index) {
        return fragments.get(index).getBinding();
    }

    public void notifyDataSetChanged(ActualExerciseNode exerciseNode) {
        setActualExerciseNode(exerciseNode);
        notifyDataSetChanged();

        for (int i = 0; i < fragments.size(); i++) {
            bindData(i);
        }
    }

    private void bindData(int index) {
        final ProgramExerciseNode programExerciseNode = actualExerciseNode.getProgramExerciseNode();
        final List<ProgramSet> programSets = programExerciseNode.getChildren();
        final List<ActualSet> actualSets = actualExerciseNode.getChildren();

        final ActualSet actualSet = index < actualSets.size()
                ? actualSets.get(index)
                : new ActualSet(actualExerciseNode.getParent().getId(), 0);

        final ProgramSet programSet = index < programSets.size() ? programSets.get(index) : null;

        Bundle arguments = new ActualSetFragment.ArgumentsBuilder()
                .setActualSet(actualSet)
                .setSetIndex(index)
                .setSetsCount(programSets.size())
                .setProgramSet(programSet)
                .setWithWeight(programExerciseNode.getExercise().isWithWeight())
                .build();

        fragments.get(index).setArguments(arguments);
    }

    private void setActualExerciseNode(ActualExerciseNode actualExerciseNode) {
        this.actualExerciseNode = actualExerciseNode;
        findCount();
    }

    private void findCount() {
        int programSetsCount = actualExerciseNode.getProgramExerciseNode().getChildren().size();
        int actualSetsCount = actualExerciseNode.getChildren().size();

        childrenCount = Math.max(programSetsCount, actualSetsCount);
    }
}
