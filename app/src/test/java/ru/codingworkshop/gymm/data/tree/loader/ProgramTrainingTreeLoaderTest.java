package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramTrainingAdapter;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.Models;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProgramTrainingTreeLoaderTest {
    @Mock private ProgramTrainingRepository repository;
    @Mock private ExercisesRepository exercisesRepository;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void load() throws Exception {
        when(repository.getProgramTrainingById(1L)).thenReturn(Flowable.just(Models.createProgramTraining(1L, "foo")));
        when(repository.getProgramExercisesForTraining(1L)).thenReturn(Flowable.just(Models.createProgramExercises(1)));
        when(repository.getProgramSetsForTraining(1L)).thenReturn(Flowable.just(Models.createProgramSets(2L, 3)));
        when(exercisesRepository.getExercisesForProgramTraining(1L)).thenReturn(Flowable.just(Models.createExercises("bar", "baz")));

        ProgramTrainingAdapter adapter = new ProgramTrainingAdapter(repository, exercisesRepository);

        ProgramTrainingTree tree = new ImmutableProgramTrainingTree();
        ProgramTrainingTreeLoader loader = new ProgramTrainingTreeLoader(adapter);

        loader.loadById(tree, 1L).test().assertValue(loadedTree -> {
            assertEquals(1L, loadedTree.getParent().getId());
            assertEquals(2L, loadedTree.getChildren().get(0).getId());
            assertEquals(3L, loadedTree.getChildren().get(0).getChildren().get(0).getId());
            assertEquals(100L, loadedTree.getChildren().get(0).getExercise().getId());

            return true;
        });
    }
}