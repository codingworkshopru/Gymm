package ru.codingworkshop.gymm.data.tree.loader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.tree.node.ImmutableActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ActualTrainingAdapter;
import ru.codingworkshop.gymm.util.Models;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ImmutableActualTrainingTreeLoaderTest {
    @Mock private ActualTrainingAdapter adapter;

    @Test
    public void loadById() {
        when(adapter.getParent(11L)).thenReturn(Flowable.just(Models.createActualTraining(11L, 1L)));
        when(adapter.getChildren(11L)).thenReturn(Flowable.just(Models.createActualExercises(12L)));
        when(adapter.getGrandchildren(11L)).thenReturn(Flowable.just(Models.createActualSets(12L, 13L)));

        ImmutableActualTrainingTreeLoader loader = new ImmutableActualTrainingTreeLoader(adapter);
        loader.loadById(new ImmutableActualTrainingTree(), 11L)
                .test()
                .assertValue(tree -> {
                    assertEquals(11L, tree.getParent().getId());
                    assertEquals(12L, tree.getChildren().get(0).getParent().getId());
                    assertEquals(13L, tree.getChildren().get(0).getChildren().get(0).getId());

                    return true;
                });


        verify(adapter).getParent(11L);
        verify(adapter).getChildren(11L);
        verify(adapter).getGrandchildren(11L);
    }
}