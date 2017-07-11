package ru.codingworkshop.gymm.db.dao;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;

import ru.codingworkshop.gymm.db.GymmDatabase;

/**
 * Created by Радик on 20.06.2017.
 */

public abstract class DatabaseTest {
    protected GymmDatabase db;

    @Before
    public void init() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), GymmDatabase.class)
                .build();
    }

    @After
    public void end() {
        db.close();
    }
}
