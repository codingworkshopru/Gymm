package ru.codingworkshop.gymm.util;

import android.app.Application;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;

import ru.codingworkshop.gymm.App;
import ru.codingworkshop.gymm.TestApp;

/**
 * Created by Радик on 19.06.2017.
 */

public class GymmTestRunner extends AndroidJUnitRunner {
    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(cl, TestApp.class.getName(), context);
    }
}
