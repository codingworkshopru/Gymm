package ru.codingworkshop.gymm.ui.actual;

import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.ui.actual.rest.ActualTrainingRestFragment;

public class AnimationClearUtils {
    public static void clearAnimation(ActivityTestRule<? extends AppCompatActivity> activityTestRule) {
        AppCompatActivity activity = activityTestRule.getActivity();
        if (activity != null) {
            activity.getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                @Override
                public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
                    if (f instanceof ActualTrainingRestFragment) {
                        v.findViewById(R.id.restTimeLeft).clearAnimation();
                        ((ActualTrainingRestFragment) f).progressBarAnimation.setDuration(1L);
                    }
                }
            }, true);
        }
    }
}
