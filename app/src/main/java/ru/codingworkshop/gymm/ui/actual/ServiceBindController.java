package ru.codingworkshop.gymm.ui.actual;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import ru.codingworkshop.gymm.service.TrainingForegroundService;

/**
 * Created by Радик on 04.10.2017 as part of the Gymm project.
 */
public final class ServiceBindController implements ServiceConnection {
    private Context context;
    private boolean bound;
    private MutableLiveData<TrainingForegroundService> service;

    public ServiceBindController(Context context) {
        this.context = context;
        service = new MutableLiveData<>();
    }

    public LiveData<TrainingForegroundService> bindService() {
        if (!bound && TrainingForegroundService.isRunning(context)) {
            Intent intent = new Intent(context, TrainingForegroundService.class);
            context.bindService(intent, this, 0);
        }

        return service;
    }

    public void unbindService() {
        if (bound) {
            context.unbindService(this);
            bound = false;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.service.setValue(((TrainingForegroundService.ServiceBinder) service).getService());
        bound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        bound = false;
    }
}
