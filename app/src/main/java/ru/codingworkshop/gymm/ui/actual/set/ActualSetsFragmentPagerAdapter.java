package ru.codingworkshop.gymm.ui.actual.set;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.google.common.collect.Iterables;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.tree.node.ActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import timber.log.Timber;

/**
 * Created by Радик on 16.09.2017 as part of the Gymm project.
 */
public class ActualSetsFragmentPagerAdapter extends FragmentPagerAdapter {
    private ActualExerciseNode actualExerciseNode;

    public ActualSetsFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        Timber.d("Constructor call");
    }

    @Override
    public Fragment getItem(int position) {
        Timber.d("getItem: %d", position);
        ActualSetFragment fragment = ActualSetFragment.newInstance();
        bindData(position, fragment);
        return fragment;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (actualExerciseNode != null) {
            int programSetsCount = actualExerciseNode.getProgramExerciseNode().getChildren().size();
            int actualSetsCount = actualExerciseNode.getChildren().size();

            count = actualSetsCount + 1;
            if (count > programSetsCount) {
                count = programSetsCount;
            }
        }

        Timber.d("getCount(): %d", count);
        return count;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        Timber.d("destroyItem: %d", position);
    }

    public void setActualExerciseNode(ActualExerciseNode exerciseNode) {
        actualExerciseNode = exerciseNode;
        super.notifyDataSetChanged();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Timber.d("setPrimaryItem %d", position);

        if (object == null) return;

        ActualSetFragment fragment = (ActualSetFragment) object;
        if (!fragment.getUserVisibleHint()) { // if isn't primary already
            bindData(position, fragment);
        }
        super.setPrimaryItem(container, position, object);
    }

    private void bindData(int index, ActualSetFragment fragment) {
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

        fragment.setArguments(arguments);
    }
}
