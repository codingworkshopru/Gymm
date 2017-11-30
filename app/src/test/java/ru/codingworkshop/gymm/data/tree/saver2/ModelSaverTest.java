package ru.codingworkshop.gymm.data.tree.saver2;

import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ru.codingworkshop.gymm.data.tree.repositoryadapter.ParentAdapter;
import ru.codingworkshop.gymm.data.util.BiPredicate;
import ru.codingworkshop.gymm.util.Models;
import ru.codingworkshop.gymm.util.SimpleModel;
import ru.codingworkshop.gymm.util.SimpleNode;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

/**
 * Created by Radik on 28.11.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ModelSaverTest {
    @Mock private ParentAdapter<SimpleModel> adapter;
    @InjectMocks private ModelSaver<SimpleModel> saver;

    @Test
    public void saveNew() throws Exception {
        SimpleModel model = new SimpleModel(0L, "foo");
        saver.save(model);
        verify(adapter).insertParent(model);
    }

    @Test
    public void saveOld() throws Exception {
        SimpleModel model = new SimpleModel(1L, "foo");
        saver.save(model);
        verify(adapter).updateParent(model);
    }
}