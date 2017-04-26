package ru.codingworkshop.gymm.ui.program.exercise.picker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;

import com.google.common.collect.Lists;

import java.util.List;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.model.MuscleGroup;

/**
 * Created by Радик on 21.04.2017.
 */

final class MuscleGroupsPagerAdapter extends FragmentPagerAdapter {

    private List<MuscleGroupsHumanFragment> fragments;

    public MuscleGroupsPagerAdapter(FragmentActivity activity, List<MuscleGroup> muscleGroups) {
        super(activity.getSupportFragmentManager());

        List<MuscleGroup> anterior = Lists.newArrayList();
        List<MuscleGroup> posterior = Lists.newArrayList();

        for (MuscleGroup mg : muscleGroups)
            if (mg.getIsAnterior())
                anterior.add(mg);
            else
                posterior.add(mg);

        fragments = Lists.newArrayList(
                MuscleGroupsHumanFragment.newInstance(
                        activity.getString(R.string.muscles_activity_anterior),
                        R.layout.activity_program_exercise_picker_muscles_anterior,
                        R.id.imageView6,
                        anterior
                ),
                MuscleGroupsHumanFragment.newInstance(
                        activity.getString(R.string.muscles_activity_posterior),
                        R.layout.activity_program_exercise_picker_muscles_posterior,
                        R.id.imageView8,
                        posterior
                )
        );
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getTitle();
    }
}
