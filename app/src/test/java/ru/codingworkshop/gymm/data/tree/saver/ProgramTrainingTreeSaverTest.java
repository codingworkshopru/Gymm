package ru.codingworkshop.gymm.data.tree.saver;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.Models;
import ru.codingworkshop.gymm.util.TreeBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Radik on 29.11.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProgramTrainingTreeSaverTest {
    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ProgramTrainingRepository repository;
    @InjectMocks private ProgramTrainingTreeSaver saver;

    @Test
    public void saveNewTree() {
        doAnswer((Answer<Long>) invocation -> {
            ProgramTraining training = invocation.getArgument(0);
            training.setId(1L);
            return 1L;
        }).when(repository).insertProgramTraining(any(ProgramTraining.class));

        doAnswer((Answer<List<Long>>) invocation -> {
            List<ProgramExercise> programExercises = invocation.getArgument(0);
            programExercises.get(0).setId(2L);
            return Collections.singletonList(2L);
        }).when(repository).insertProgramExercises(anyCollection());

        ProgramTrainingTree tree = TreeBuilders.buildProgramTrainingTree(1);
        tree.getParent().setId(0L);
        tree.getChildren().get(0).getParent().setId(0L);
        tree.getChildren().get(0).getChildren().get(0).setId(0L);

        saver.save(tree).test().assertComplete();

        verify(repository).insertProgramTraining(tree.getParent());

        verify(repository).insertProgramExercises(argThat(ex -> {
            ProgramExercise exercise = ex.iterator().next();
            return exercise.getProgramTrainingId() == 1L;
        }));
        verify(repository).insertProgramSets(argThat(sets -> {
            ProgramSet set = sets.iterator().next();
            return set.getProgramExerciseId() == 2L;
        }));
    }

    @Test
    public void save() {
        when(repository.getProgramExercisesForTraining(1L)).thenReturn(Collections.emptyList());
        when(repository.getProgramSetsForTraining(1L)).thenReturn(Collections.emptyList());

        doAnswer((Answer<List<Long>>) invocation -> {
            List<ProgramExercise> programExercises = invocation.getArgument(0);
            programExercises.get(0).setId(2L);
            return Collections.singletonList(2L);
        }).when(repository).insertProgramExercises(anyCollection());

        ProgramTrainingTree tree = new ImmutableProgramTrainingTree();
        tree.setParent(Models.createProgramTraining(1L, "foo"));
        ProgramExercise programExercise = new ProgramExercise();
        programExercise.setProgramTrainingId(1L);
        programExercise.setExerciseId(100L);
        ImmutableProgramExerciseNode programExerciseNode = new ImmutableProgramExerciseNode(programExercise);
        ProgramSet set = new ProgramSet();
        programExerciseNode.setChildren(Collections.singletonList(set));
        tree.setChildren(Collections.singletonList(programExerciseNode));

        saver.save(tree).test().assertComplete();

        verify(repository).updateProgramTraining(any(ProgramTraining.class));
        verify(repository).insertProgramExercises(anyCollection());
        verify(repository).insertProgramSets(argThat(programSet -> programSet.iterator().next().getProgramExerciseId() == 2L));
    }
}