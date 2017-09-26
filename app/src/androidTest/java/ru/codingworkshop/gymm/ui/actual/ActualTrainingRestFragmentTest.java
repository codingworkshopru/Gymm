package ru.codingworkshop.gymm.ui.actual;

import android.support.test.rule.ActivityTestRule;

import com.google.common.eventbus.EventBus;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.service.event.incoming.StartRestEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestTimerTickEvent;
import ru.codingworkshop.gymm.testing.SimpleFragmentActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by Радик on 23.09.2017 as part of the Gymm project.
 */

public class ActualTrainingRestFragmentTest {
    @Rule public ActivityTestRule<SimpleFragmentActivity> activityTestRule =
            new ActivityTestRule<>(SimpleFragmentActivity.class);

    private ActualTrainingRestFragment fragment;
    private EventBus bus;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        fragment = ActualTrainingRestFragment.newInstance(30000);
        bus = spy(new EventBus());
        fragment.restEventBus = bus;
        activityTestRule.getActivity().setFragment(fragment);
        onView(withId(R.id.restTimeLeft)).check(matches(withText("0:30")));
    }

    @Test
    public void initializationTest() throws Exception {
        verify(bus).post(any(StartRestEvent.class));
    }

    @Test
    public void eventBusRegister() throws Exception {
        verify(bus).register(any());
    }

    @Test
    public void setCurrentTime() throws Exception {
        bus.post(new RestTimerTickEvent(20000));
        onView(withId(R.id.restTimeLeft)).check(matches(withText("0:20")));
    }
}
