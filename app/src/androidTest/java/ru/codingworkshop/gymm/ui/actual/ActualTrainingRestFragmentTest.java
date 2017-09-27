package ru.codingworkshop.gymm.ui.actual;

import android.support.test.rule.ActivityTestRule;

import com.google.common.eventbus.EventBus;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import ru.codingworkshop.gymm.R;
import ru.codingworkshop.gymm.service.event.incoming.AddRestTimeEvent;
import ru.codingworkshop.gymm.service.event.incoming.PauseRestEvent;
import ru.codingworkshop.gymm.service.event.incoming.ResumeRestEvent;
import ru.codingworkshop.gymm.service.event.incoming.StartRestEvent;
import ru.codingworkshop.gymm.service.event.incoming.StopRestEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestFinishedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestPausedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestResumedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestTimeAddedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestTimerTickEvent;
import ru.codingworkshop.gymm.testing.SimpleFragmentActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
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

        doAnswer(invocation -> {
            bus.post(new RestResumedEvent(25000));
            return null;
        }).when(bus).post(any(ResumeRestEvent.class));

        doAnswer(invocation -> {
            bus.post(new RestPausedEvent());
            return null;
        }).when(bus).post(any(PauseRestEvent.class));

        doAnswer(invocation -> {
            bus.post(new RestTimeAddedEvent(90000));
            return null;
        }).when(bus).post(any(AddRestTimeEvent.class));

        assertTime("0:30");
    }

    @Test
    public void initializationTest() throws Exception {
        verify(bus).register(fragment);
        verify(bus).post(any(StartRestEvent.class));

        onView(withId(R.id.restPauseResumeActionButton)).check(matches(Matchers.hasImage(R.drawable.ic_pause_white_24dp)));
    }

    @Test
    public void plusOneMinute() throws Exception {
        onView(withId(R.id.restPlusOneMinuteButton)).perform(click());
        verify(bus).post(any(AddRestTimeEvent.class));

        assertTime("1:30");
    }

    @Test
    public void stopRest() throws Exception {
        onView(withId(R.id.restStopButton)).perform(click());
        verify(bus).post(any(StopRestEvent.class));
    }

    @Test
    public void restFinished() throws Exception {
        bus.post(new RestFinishedEvent());

        assertTime("0:00");
        onView(withId(R.id.restStopButton)).check(matches(not(isDisplayed())));
        onView(withId(R.id.restPlusOneMinuteButton)).check(matches(not(isDisplayed())));
        onView(withId(R.id.restPauseResumeActionButton)).check(matches(Matchers.hasImage(R.drawable.ic_stop_white_24dp)));
    }

    @Test
    public void pauseRest() throws Exception {
        onView(withId(R.id.restPauseResumeActionButton)).perform(click());
        verify(bus).post(any(PauseRestEvent.class));

        onView(withId(R.id.restPauseResumeActionButton)).check(matches(
                Matchers.hasImage(R.drawable.ic_play_arrow_white_24dp)));
    }

    @Test
    public void resumeRest() throws Exception {
        onView(withId(R.id.restPauseResumeActionButton)).perform(click(), click());
        verify(bus).post(any(ResumeRestEvent.class));

        onView(withId(R.id.restPauseResumeActionButton)).check(matches(
                Matchers.hasImage(R.drawable.ic_pause_white_24dp)));
    }

    @Test
    public void setCurrentTime() throws Exception {
        bus.post(new RestTimerTickEvent(20000));
        assertTime("0:20");
    }

    private void assertTime(String time) {
        onView(withId(R.id.restTimeLeft)).check(matches(withText(time)));
    }
}
