package ru.codingworkshop.gymm.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.codingworkshop.gymm.data.util.Consumer;
import ru.codingworkshop.gymm.service.event.incoming.AddRestTimeEvent;
import ru.codingworkshop.gymm.service.event.incoming.PauseRestEvent;
import ru.codingworkshop.gymm.service.event.incoming.ResumeRestEvent;
import ru.codingworkshop.gymm.service.event.incoming.StartRestEvent;
import ru.codingworkshop.gymm.service.event.incoming.StopRestEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestFinishedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestPausedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestResumedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestStartedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestStoppedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestTimeAddedEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestTimerTickEvent;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

/**
 * Created by Радик on 21.09.2017 as part of the Gymm project.
 */

public class TrainingForegroundServiceTest {
    @Rule
    public ServiceTestRule serviceTestRule = new ServiceTestRule();

    @Test
    public void isRunningTest() throws Exception {
        final Context targetContext = InstrumentationRegistry.getTargetContext();
        assertFalse(TrainingForegroundService.isRunning(targetContext));
        serviceTestRule.startService(getIntent());
        assertTrue(TrainingForegroundService.isRunning(targetContext));
    }

    @Test
    public void startRest() throws Exception {
        TrainingForegroundService service = bind();
        assertFalse(service.isRestInProgress());

        postEvent(service, new StartRestEvent(2000));
        assertTrue(service.isRestInProgress());
        assertEventReceived(service, RestStartedEvent.class, e -> assertEquals(2000, e.getMilliseconds()));
    }

    @Test
    public void restFinished() throws Exception {
        TrainingForegroundService service = bind();

        postEvent(service, new StartRestEvent(2000));
        assertEventReceived(service, RestFinishedEvent.class, e -> assertFalse(service.isRestInProgress()), 3000);
    }

    @Test
    public void onTick() throws Exception {
        final TrainingForegroundService service = bind();

        postEvent(service, new StartRestEvent(2000));
        Thread.sleep(100);
        assertEventReceived(service, RestTimerTickEvent.class,
                e -> assertThat(e.getMilliseconds(), both(greaterThan(0L)).and(lessThan(2000L))));
    }

    @Test
    public void getMillisecondsLeft() throws Exception {
        final TrainingForegroundService service = bind();
        postEvent(service, new StartRestEvent(2000));
        Thread.sleep(1100);
        assertThat(service.getMillisecondsLeft(), both(greaterThan(0L)).and(lessThan(2000L)));
    }

    @Test
    public void pauseRest() throws Exception {
        final TrainingForegroundService service = bind();

        postEvent(service, new StartRestEvent(2000));
        assertFalse(service.isRestInPause());
        postEvent(service, new PauseRestEvent());
        assertTrue(service.isRestInPause());

        assertEventReceived(service, RestPausedEvent.class, null);

        assertEventsNotReceived(service, null, 1000L, RestTimerTickEvent.class, RestFinishedEvent.class);
    }

    @Test
    public void resumeRest() throws Exception {
        final TrainingForegroundService service = bind();
        postEvent(service, new StartRestEvent(2000));
        postEvent(service, new PauseRestEvent());
        assertEventsNotReceived(service, null, 2100L, RestTimerTickEvent.class, RestFinishedEvent.class);
        postEvent(service, new ResumeRestEvent());
        assertEventReceived(service, RestResumedEvent.class,
                e -> assertEquals(2000L, e.getMilliseconds()));
        assertEventReceived(service, RestFinishedEvent.class, null, 2100);
    }

    @Test
    public void stopRest() throws Exception {
        final TrainingForegroundService service = bind();
        postEvent(service, new StartRestEvent(1000));
        postEvent(service, new StopRestEvent());
        assertEventReceived(service, RestStoppedEvent.class, e -> assertFalse(service.isRestInProgress()));
    }

    @Test
    public void stopPausedRest() throws Exception {
        final TrainingForegroundService service = bind();
        postEvent(service, new StartRestEvent(1000));
        postEvent(service, new PauseRestEvent());
        postEvent(service, new StopRestEvent());
        assertEventReceived(service, RestStoppedEvent.class, null);
        assertFalse(service.isRestInProgress());
    }

    @Test
    public void addMoreTimeForRest() throws Exception {
        final TrainingForegroundService service = bind();
        postEvent(service, new StartRestEvent(1000));
        postEvent(service, new AddRestTimeEvent(2000));
        assertEventReceived(service, RestTimeAddedEvent.class, e -> assertEquals(2000, e.getMilliseconds()));
        assertEventsNotReceived(service, null, 1100, RestFinishedEvent.class);
    }

    private TrainingForegroundService bind() throws TimeoutException {
        return bind(getIntent());
    }

    private TrainingForegroundService bind(Intent intent) throws TimeoutException {
        IBinder binder = serviceTestRule.bindService(intent);
        return ((TrainingForegroundService.ServiceBinder)binder).getService();
    }

    @NonNull
    private Intent getIntent() {
        return getIntent(TrainingForegroundService.class);
    }

    private Intent getIntent(Class<?> serviceClass) {
        return new Intent(InstrumentationRegistry.getTargetContext(), serviceClass);
    }

    private <T> void postEvent(TrainingForegroundService service, T event) {
        EventBus restEventBus = service.getRestEventBus();
        restEventBus.post(event);
    }

    private <T> void assertEventReceived(TrainingForegroundService service, Class<T> eventClass, Consumer<T> consumer) throws InterruptedException {
        assertTrue("event " + eventClass.getSimpleName() + " haven't been received",
                assertEvent(service, Lists.newArrayList(eventClass), consumer, 1000L));
    }

    private <T> void assertEventReceived(TrainingForegroundService service, Class<T> eventClass, Consumer<T> consumer, long timeout) throws InterruptedException {
        assertTrue("event " + eventClass.getSimpleName() + " haven't been received",
                assertEvent(service, Lists.newArrayList(eventClass), consumer, timeout));
    }

    private <T> void assertEventsNotReceived(TrainingForegroundService service, Consumer<T> consumer, long timeout, Class... eventClasses) throws InterruptedException {
        assert eventClasses.length != 0;
        List<Class> classesList = Lists.newArrayList(eventClasses);
        assertFalse("event " + classesList + " have been received",
                assertEvent(service, classesList, consumer, timeout));
    }

    private <T> boolean assertEvent(TrainingForegroundService service, List<Class> eventClasses, Consumer<T> consumer, long timeout) throws InterruptedException {
        final EventBus restEventBus = service.getRestEventBus();
        AtomicBoolean eventReceived = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);
        restEventBus.register(new Object() {
            @Subscribe
            private void onEvent(T o) {
                if (eventClasses.contains(o.getClass())) {
                    if (consumer != null) {
                        consumer.accept(o);
                    }
                    eventReceived.set(true);
                    latch.countDown();
                }
            }
        });
        latch.await(timeout, TimeUnit.MILLISECONDS);
        return eventReceived.get();
    }
}
