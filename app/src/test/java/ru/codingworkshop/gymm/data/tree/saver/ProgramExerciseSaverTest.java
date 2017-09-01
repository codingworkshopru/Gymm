package ru.codingworkshop.gymm.data.tree.saver;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.common.collect.Lists;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.stream.Collectors;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.Models;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 25.08.2017 as part of the Gymm project.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProgramExerciseSaverTest {
    @Mock private ProgramTrainingRepository repository;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Test
    public void programExerciseInsert() throws Exception {
        final ProgramExercise programExercise = Models.createProgramExercise(0L, 1L, 100L, true);
        ProgramExerciseNode node = new MutableProgramExerciseNode(programExercise);
        ProgramExerciseSaver saver = new ProgramExerciseSaver(node, repository);
        saver.saveParent();
        verify(repository).insertProgramExercise(programExercise);
    }

    @Test
    public void save() throws Exception {
        ProgramExercise programExercise = Models.createProgramExercise(2L, 1L, 100L, false);
        ProgramExerciseNode node = new MutableProgramExerciseNode(programExercise);
        List<ProgramSet> programSets = Models.createProgramSets(2L, 5);
        node.setChildren(programSets);
        node.moveChild(2,3);
        node.getChildren().get(0).setReps(100);
        node.removeChild(4);
        ProgramSet addedProgramSet = Models.createProgramSet(0L, 2L, 90);
        node.addChild(addedProgramSet);

        when(repository.getProgramSetsForExercise(programExercise)).thenReturn(LiveDataUtil.getLive(Models.createProgramSets(2L, 5)));

        ProgramExerciseSaver saver = new ProgramExerciseSaver(node, repository);
        saver.save();

        verify(repository).updateProgramExercise(programExercise);
        verify(repository).getProgramSetsForExercise(programExercise);

        verify(repository).insertProgramSets(Lists.newArrayList(addedProgramSet));
        verify(repository).deleteProgramSets(Lists.newArrayList(programSets.get(4)));
        verify(repository).updateProgramSets(Lists.newArrayList(0,3,2).stream().map(programSets::get).collect(Collectors.toList()));
    }
}