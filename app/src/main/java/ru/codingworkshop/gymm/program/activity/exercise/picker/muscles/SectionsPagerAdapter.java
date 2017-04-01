package ru.codingworkshop.gymm.program.activity.exercise.picker.muscles;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.model.MuscleGroup;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private List<MuscleGroup> muscleGroups;
    private Context context;

    public SectionsPagerAdapter(FragmentManager fm, List<MuscleGroup> muscleGroups, Context context) {
        super(fm);
        this.muscleGroups = muscleGroups;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return PlaceholderFragment.newInstance(position, muscleGroups);
    }

    @Override
    public int getCount() {
        // Show anterior and posterior.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.muscles_activity_anterior);
            case 1:
                return context.getString(R.string.muscles_activity_posterior);
        }
        return null;
    }
}
