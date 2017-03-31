package ru.codingworkshop.gymm.program.activity.exercise.picker.muscles;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import ru.codingworkshop.gymm.data.model.MuscleGroup;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private List<MuscleGroup> muscleGroups;

    public SectionsPagerAdapter(FragmentManager fm, List<MuscleGroup> muscleGroups) {
        super(fm);
        this.muscleGroups = muscleGroups;
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
                return "СПЕРЕДИ";
            case 1:
                return "СЗАДИ";
        }
        return null;
    }
}
