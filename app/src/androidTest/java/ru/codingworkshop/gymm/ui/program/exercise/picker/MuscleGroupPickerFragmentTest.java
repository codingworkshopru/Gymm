package ru.codingworkshop.gymm.ui.program.exercise.picker;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.IdRes;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.testing.SimpleFragmentActivity;
import ru.codingworkshop.gymm.ui.Matchers;
import ru.codingworkshop.gymm.util.Models;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Radik on 22.11.2017.
 */

public class MuscleGroupPickerFragmentTest {
    @Rule public ActivityTestRule<SimpleFragmentActivity> activityTestRule =
            new ActivityTestRule<>(SimpleFragmentActivity.class);

    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ViewModelProvider.Factory viewModelFactory;
    @Mock private MuscleGroupPickerViewModel vm;
    @Mock private MuscleGroupPickerFragment.OnMuscleGroupPickListener listener;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(viewModelFactory.create(any())).thenReturn(vm);
        LiveData<List<MuscleGroup>> liveMuscleGroups = Models.createLiveMuscleGroups(200L);
        liveMuscleGroups.getValue().get(0).setMapColorRgb("#0066CC");
        when(vm.load(false)).thenReturn(liveMuscleGroups);

        MuscleGroupPickerFragment fragment = MuscleGroupPickerFragment.newInstance(false);
        fragment.viewModelFactory = viewModelFactory;
        fragment.listener = listener;
        activityTestRule.getActivity().setFragment(fragment);
    }

    @Test
    public void humanMusclesImageVisibilityTest() throws Exception {
        onView(withId(R.id.muscleGroupPickerHumanMuscles)).check(matches(Matchers.hasImage(R.drawable.human_muscles_posterior)));
    }

    @Test
    public void tapOnMuscleGroupTest() throws Exception {
        onView(withId(R.id.muscleGroupPickerHumanMuscles)).perform(clickOnMuscleGroupWithColor("#0066CC"));
        verify(listener).onMuscleGroupPick(argThat(mg -> mg.getId() == 200L));
    }

    public static GeneralClickAction clickOnMuscleGroupWithColor(String color) {
        return new GeneralClickAction(
                Tap.SINGLE,
                getCoordinatesForColor(color, R.id.muscleGroupPickerMap),
                Press.FINGER,
                InputDevice.SOURCE_UNKNOWN,
                MotionEvent.BUTTON_PRIMARY
        );
    }

    private static CoordinatesProvider getCoordinatesForColor(final String htmlRgbColor, @IdRes int searchImageView) {
        return view -> {
            View v = view.getRootView().findViewById(searchImageView);
            if (v instanceof ImageView) {
                int parsedColor = Color.parseColor(htmlRgbColor);
                ImageView iw = (ImageView) v;

                iw.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(iw.getDrawingCache());
                iw.setDrawingCacheEnabled(false);
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();

                Point topLeft = null;

                for (int x = 0; x < width; x++) {
                    if (topLeft != null) {
                        break;
                    }
                    for (int y = 0; y < height; y++) {
                        if (bitmap.getPixel(x, y) == parsedColor) {
                            topLeft = new Point(x, y);
                            break;
                        }
                    }
                }

                if (topLeft == null) {
                    throw new IllegalArgumentException("color " + htmlRgbColor + " not found");
                }

                Point bottomRight = new Point();
                for (int x = topLeft.x; bitmap.getPixel(x, topLeft.y) == parsedColor && x < width; x++) {
                    bottomRight.set(x, 0);
                }

                for (int y = topLeft.y; bitmap.getPixel(topLeft.x, y) == parsedColor && y < height; y++) {
                    bottomRight.set(bottomRight.x, y);
                }

                Rect r = new Rect(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y);

                int[] screenLocation = new int[2];
                view.getLocationOnScreen(screenLocation);
                float[] result = {
                        screenLocation[0] + r.centerX(),
                        screenLocation[1] + r.centerY()
                };
                Log.d("FROM TEST", "tap coordinates are " + result[0] + " " + result[1] + " expected color is " + parsedColor);
                return result;
            } else {
                throw new IllegalArgumentException("the view in matcher must be an ImageView instance");
            }
        };
    }
}