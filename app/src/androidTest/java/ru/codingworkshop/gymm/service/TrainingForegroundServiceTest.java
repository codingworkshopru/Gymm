package ru.codingworkshop.gymm.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;

import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeoutException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

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
        service.startRest(1000);
        Thread.sleep(100);
        assertTrue(service.isRestInProgress());
    }

    @Test
    public void restFinished() throws Exception {
        TrainingForegroundService service = bind();
        service.startRest(1);
        Thread.sleep(100);
        assertFalse(service.isRestInProgress());
    }

    @Test
    public void getMillisecondsLeft() throws Exception {
        final TrainingForegroundService service = bind();
        service.startRest(2000);
        Thread.sleep(1100);
        long millis = service.getMillisecondsLeft();
        assertTrue(millis < 2000 && millis > 0);
    }

    @Test
    public void pauseRest() throws Exception {
        final TrainingForegroundService service = bind();
        service.startRest(2000);
        Thread.sleep(1100);
        long millis = service.getMillisecondsLeft();
        service.pauseRest();
        assertTrue(service.isRestInPause());
        Thread.sleep(1100);
        assertEquals(millis, service.getMillisecondsLeft());
    }

    @Test
    public void resumeRest() throws Exception {
        final TrainingForegroundService service = bind();
        service.startRest(1000);
        service.pauseRest();
        assertTrue(service.isRestInPause());
        service.resumeRest();
        assertFalse(service.isRestInPause());
        assertTrue(service.isRestInProgress());
        Thread.sleep(1100);
        assertFalse(service.isRestInProgress());
    }

    @Test
    public void stopRest() throws Exception {
        final TrainingForegroundService service = bind();
        service.startRest(1000);
        service.stopRest();
        assertFalse(service.isRestInProgress());
        assertEquals(0, service.getMillisecondsLeft());
    }

    @Test
    public void stopPausedRest() throws Exception {
        final TrainingForegroundService service = bind();
        service.startRest(1000);
        service.pauseRest();
        service.stopRest();
        assertFalse(service.isRestInProgress());
    }

    @Test
    public void addMoreTimeForRest() throws Exception {
        final TrainingForegroundService service = bind();
        service.startRest(1000);
        service.addRestTime(2000);
        Thread.sleep(2000);
        assertTrue(service.isRestInProgress());
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
}
