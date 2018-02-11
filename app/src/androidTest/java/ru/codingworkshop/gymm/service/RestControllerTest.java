package ru.codingworkshop.gymm.service;

import android.os.HandlerThread;
import android.os.Looper;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ru.codingworkshop.gymm.service.event.incoming.AddRestTimeEvent;
import ru.codingworkshop.gymm.service.event.incoming.PauseRestEvent;
import ru.codingworkshop.gymm.service.event.incoming.ResumeRestEvent;
import ru.codingworkshop.gymm.service.event.incoming.StartRestEvent;
import ru.codingworkshop.gymm.service.event.incoming.StopRestEvent;
import ru.codingworkshop.gymm.service.event.outcoming.RestTimerTickEvent;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class RestControllerTest {

    private RestController restControllerIdling;

    @Before
    public void setUp() throws Exception {
        HandlerThread thread = new HandlerThread("TestHandlerThread");
        thread.start();
        Looper looper = thread.getLooper();
        restControllerIdling = new RestController(looper);
    }

    @Test
    public void startRest() throws InterruptedException {
        assertFalse(restControllerIdling.isRestInProgress());
        postEvent(new StartRestEvent(2000));
        assertTrue(restControllerIdling.isRestInProgress());
    }

    @Test
    public void pauseRest() throws InterruptedException {
        postEvent(new StartRestEvent(2000));
        assertFalse(restControllerIdling.isRestInPause());
        postEvent(new PauseRestEvent());
        assertTrue(restControllerIdling.isRestInPause());
    }

    @Test
    public void resumeRest() throws InterruptedException {
        postEvent(new StartRestEvent(3000));
        postEvent(new PauseRestEvent());
        assertTrue(restControllerIdling.isRestInPause());
        postEvent(new ResumeRestEvent());
        assertFalse(restControllerIdling.isRestInPause());
        assertTrue(restControllerIdling.isRestInProgress());
    }

    @Test
    public void finishRest() throws InterruptedException {
        postEvent(new StartRestEvent(2000));
        postEvent(new StopRestEvent());
        assertFalse(restControllerIdling.isRestInProgress());
    }

    @Test
    public void addRestTime() throws InterruptedException {
        postEvent(new StartRestEvent(5000));
        postEvent(new AddRestTimeEvent(10000));
        assertThat(restControllerIdling.getMillisecondsLeft(), greaterThan(10000L));
    }
    
    private void postEvent(Object event) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        EventBus eventBus = restControllerIdling.getRestEventBus();
        eventBus.register(new Object() {
            @SuppressWarnings("unused")
            @Subscribe
            private void handler(Object event) {
                if (event.getClass().isAnnotationPresent(RestController.OutcomeEvent.class)
                        && !event.getClass().equals(RestTimerTickEvent.class))
                {
                    countDownLatch.countDown();
                }
            }
        });
        eventBus.post(event);
        countDownLatch.await(5, TimeUnit.SECONDS);
    }
}