package ru.codingworkshop.gymm.data.tree.saver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ru.codingworkshop.gymm.data.entity.common.Model;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 24.08.2017 as part of the Gymm project.
 */

@RunWith(MockitoJUnitRunner.class)
public class ModelSaverTest {
    @Mock private ModelSaver.ModelSaverCallback<Model> callback;
    @Mock private Model model;

    private ModelSaver<Model> saver;

    @Before
    public void setUp() throws Exception {
        saver = new ModelSaver<>(model, callback);
    }

    @Test
    public void insert() throws Exception {
        when(model.getId()).thenReturn(0L);
        saver.save();
        verify(callback).insertParent(model);
    }

    @Test
    public void update() throws Exception {
        when(model.getId()).thenReturn(1L);
        saver.save();
        verify(callback).updateParent(model);
    }
}