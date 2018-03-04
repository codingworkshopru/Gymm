package ru.codingworkshop.gymm.data.util;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import ru.codingworkshop.gymm.data.tree.loader.common.Loader;
import ru.codingworkshop.gymm.util.SimpleNode;

import static junit.framework.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoaderAdapterTest {
    @Mock private Loader<SimpleNode> loader;
    @Mock private SimpleNode node;
    @InjectMocks private LoaderAdapter<SimpleNode> adapter;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        when(loader.loadById(any(SimpleNode.class), eq(1L))).thenAnswer((Answer<Flowable<SimpleNode>>) invocation -> {
            SimpleNode argument = invocation.getArgument(0);
            return Flowable.just(argument);
        });
    }

    @Test
    public void load() {
        LiveData<SimpleNode> liveData = adapter.load(1L);
        assertSame(liveData, adapter.load(1L));
        verify(loader).loadById(any(SimpleNode.class), eq(1L));
    }

    @Test
    public void clear() {
        Disposable disposable = mock(Disposable.class);
        when(disposable.isDisposed()).thenReturn(false);
        adapter.disposable = disposable;
        adapter.clear();
        verify(disposable).dispose();
    }
}