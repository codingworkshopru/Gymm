package ru.codingworkshop.gymm.data.tree.saver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ru.codingworkshop.gymm.data.tree.repositoryadapter.SingleModelAlterAdapter;
import ru.codingworkshop.gymm.util.SimpleModel;

import static org.mockito.Mockito.verify;

/**
 * Created by Radik on 28.11.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ModelSaverTest {
    @Mock private SingleModelAlterAdapter<SimpleModel> adapter;
    @InjectMocks private ModelSaver<SimpleModel> saver;

    @Test
    public void saveNew() {
        SimpleModel model = new SimpleModel(0L, "foo");
        saver.save(model);
        verify(adapter).insert(model);
    }

    @Test
    public void saveOld() {
        SimpleModel model = new SimpleModel(1L, "foo");
        saver.save(model);
        verify(adapter).update(model);
    }
}