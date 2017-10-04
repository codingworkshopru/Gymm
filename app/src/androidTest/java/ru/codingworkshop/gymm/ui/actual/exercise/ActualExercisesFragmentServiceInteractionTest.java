package ru.codingworkshop.gymm.ui.actual.exercise;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import ru.codingworkshop.gymm.service.TrainingForegroundService;
import ru.codingworkshop.gymm.service.event.incoming.StartRestEvent;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static ru.codingworkshop.gymm.ui.actual.TreeBuilders.buildHalfPopulatedTree;

/**
 * Created by Радик on 04.10.2017 as part of the Gymm project.
 */

public class ActualExercisesFragmentServiceInteractionTest extends Base {
    @Rule
    public ServiceTestRule serviceTestRule = new ServiceTestRule();

    @Override
    void beforeFragmentSet() throws Exception {
        setFakeTree(buildHalfPopulatedTree(3));

        final Context targetContext = InstrumentationRegistry.getTargetContext();
        Intent intent = new Intent(targetContext, TrainingForegroundService.class);

        final Bundle extras = TrainingForegroundService.createExtras(11L, "test");
        intent.putExtras(extras);
        serviceTestRule.startService(intent);

        IBinder binder = serviceTestRule.bindService(intent);
        TrainingForegroundService service = ((TrainingForegroundService.ServiceBinder) binder).getService();
        service.getRestEventBus().post(new StartRestEvent(60000));
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void restResumeOnFragmentCreationTest() throws Exception {
        onView(withText("foo")).check(matches(isDisplayed()));
        verify(callback).onStartRest(anyInt());
    }
}
