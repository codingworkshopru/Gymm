package ru.codingworkshop.gymm.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import ru.codingworkshop.gymm.service.event.MillisecondsEvent;
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
    @ClassRule
    public static ServiceTestRule serviceTestRule;

    private Map<Class, Long> postedEvents = Collections.synchronizedMap(new HashMap<>());
    private Vector<Observer> observers = new Vector<>();

    private TrainingForegroundService service;

    @BeforeClass
    public static void setUpClass() throws Exception {
        serviceTestRule = new ServiceTestRule();
        serviceTestRule.startService(getIntent(11L, "foo"));
    }

    @AfterClass
    public static void tearDownClass() {
        assert InstrumentationRegistry.getTargetContext().stopService(getIntent());
    }

    @Before
    public void setUp() throws Exception {
        service = bind();
        service.getRestEventBus().register(new Object() {
            @SuppressWarnings("unused")
            @Subscribe
            private void onEvent(Object event) {
                long millis = -1L;
                if (event instanceof MillisecondsEvent) {
                    millis = ((MillisecondsEvent) event).getMilliseconds();
                }
                postedEvents.put(event.getClass(), millis);
                for (Observer o : observers) {
                    o.update(null, event);
                }
            }
        });
    }

    @After
    public void tearDown() {
        postedEvents.clear();
        observers.clear();
        postEvent(new StopRestEvent());
        serviceTestRule.unbindService();
    }

    @Test
    public void isRunningTest() {
        final Context targetContext = InstrumentationRegistry.getTargetContext();
        assertTrue(TrainingForegroundService.isRunning(targetContext));
    }

    @Test
    public void startRest() throws Exception {
        assertFalse(service.isRestInProgress());

        postEvent(new StartRestEvent(2000));
        assertEventReceived(RestStartedEvent.class, 1000L, 2000L);
        assertTrue(service.isRestInProgress());
    }

    @Test
    public void restFinished() throws Exception {
        postEvent(new StartRestEvent(1));
        assertEventReceived(RestFinishedEvent.class, 1000L);
    }

    @Test
    public void onTick() throws Exception {
        postEvent(new StartRestEvent(2000));
        assertEventReceived(RestTimerTickEvent.class, 1100L, 1000L, 2000L);
    }

    @Test
    public void getMillisecondsLeft() throws Exception {
        postEvent(new StartRestEvent(2000));
        assertEventReceived(RestTimerTickEvent.class);
        assertThat(service.getMillisecondsLeft(), both(greaterThan(0L)).and(lessThan(2000L)));
    }

    @Test
    public void pauseRest() throws Exception {
        postEvent(new StartRestEvent(2000));
        assertFalse(service.isRestInPause());
        postEvent(new PauseRestEvent());
        assertEventReceived(RestPausedEvent.class);
        assertTrue(service.isRestInPause());
    }

    @Test
    public void resumeRest() throws Exception {
        postEvent(new StartRestEvent(2000L));
        postEvent(new PauseRestEvent());
        postEvent(new ResumeRestEvent());
        assertEventReceived(RestResumedEvent.class, 1000L, 1000L, 2001L);
        assertTrue(service.isRestInProgress());
    }

    @Test
    public void stopRest() throws Exception {
        postEvent(new StartRestEvent(1000));
        postEvent(new StopRestEvent());
        assertEventReceived(RestStoppedEvent.class);
        assertFalse(service.isRestInProgress());
    }

    @Test
    public void stopPausedRest() throws Exception {
        postEvent(new StartRestEvent(2000));
        postEvent(new PauseRestEvent());
        postEvent(new StopRestEvent());
        assertEventReceived(RestStoppedEvent.class);
        assertFalse(service.isRestInProgress());
    }

    @Test
    public void addMoreTimeForRest() throws Exception {
        postEvent(new StartRestEvent(1000));
        postEvent(new AddRestTimeEvent(3000));
        assertEventReceived(RestTimeAddedEvent.class, 1000L, 3000L, 4001L);
        assertThat(service.getMillisecondsLeft(), greaterThan(2000L));
    }

    private TrainingForegroundService bind() throws TimeoutException {
        return bind(getIntent());
    }

    private TrainingForegroundService bind(Intent intent) throws TimeoutException {
        IBinder binder = serviceTestRule.bindService(intent);
        return ((TrainingForegroundService.ServiceBinder)binder).getService();
    }

    private static Intent getIntent() {
        return new Intent(InstrumentationRegistry.getTargetContext(), TrainingForegroundService.class);
    }

    private static Intent getIntent(long id, String name) {
        Intent intent = getIntent();
        intent.putExtras(TrainingForegroundService.createExtras(id, name));
        return intent;
    }

    private <T> void postEvent(T event) {
        EventBus restEventBus = service.getRestEventBus();
        restEventBus.post(event);
    }

    private void assertEventReceived(Class eventType) throws InterruptedException {
        assertEventReceived(eventType, 1000L);
    }

    private void assertEventReceived(Class eventType, long timeout) throws InterruptedException {
        assertEventReceived(eventType, timeout, -1L, -1L);
    }

    private void assertEventReceived(Class eventType, long timeout, long expected) throws InterruptedException {
        assertEventReceived(eventType, timeout, expected, expected);
    }

    private void assertEventReceived(Class eventType, long timeout, long expectedMin, long expectedMax) throws InterruptedException {
        AtomicBoolean ok = new AtomicBoolean(false);
        AtomicLong l = new AtomicLong(-1L);
        if (postedEvents.containsKey(eventType)) {
            ok.set(true);
            l.set(postedEvents.get(eventType));
        }

        if (!ok.get()) {
            CountDownLatch latch = new CountDownLatch(1);
            observers.add((o, arg) -> {
                if (arg.getClass().equals(eventType)) {
                    if (arg instanceof MillisecondsEvent) {
                        l.set(((MillisecondsEvent) arg).getMilliseconds());
                    } else {
                        l.set(-1);
                    }
                    ok.set(true);
                    latch.countDown();
                }
            });

            latch.await(timeout, TimeUnit.MILLISECONDS);
        }

        assertTrue("event " + eventType.getSimpleName() + " haven't been received", ok.get());

        if (expectedMin == -1) {
            return;
        }

        if (expectedMin == expectedMax) {
            assertEquals("expected value is not equals to actual one", expectedMin, l.get());
        } else {
            assertThat("actual value doesn't fit in bounds", l.get(),
                    both(greaterThan(expectedMin)).and(lessThan(expectedMax)));
        }
    }
}
