package ru.codingworkshop.gymm.data.tree.saver;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.stream.Collectors;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.MutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.Models;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by Радик on 25.08.2017 as part of the Gymm project.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProgramTrainingSaverTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private ProgramTrainingRepository repository;
    private ProgramTrainingTree tree;

    @Before
    public void setUp() throws Exception {
        tree = new MutableProgramTrainingTree();
    }

    @Test
    public void programTrainingInsert() throws Exception {
        ProgramTraining programTraining = Models.createProgramTraining(0L, "foo");
        tree.setParent(programTraining);

        ProgramTrainingSaver saver = new ProgramTrainingSaver(tree, repository);
        saver.saveParent();

        verify(repository).insertProgramTraining(programTraining);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void save() throws Exception {
        ProgramTraining programTraining = Models.createProgramTraining(1L, "foo");
        tree.setParent(programTraining);

        final List<ProgramExercise> oldChildren = Models.createProgramExercises(3);
        final List<ProgramExercise> newChildren = Models.createProgramExercises(3);
        List<ProgramExerciseNode> children = newChildren.stream().map(MutableProgramExerciseNode::new).collect(Collectors.toList());
        tree.setChildren(children);
        tree.removeChild(2);
        tree.addChild(new MutableProgramExerciseNode(Models.createProgramExercise(9L, 1L, 109L, false)));
        tree.moveChild(0,1);

        when(repository.getProgramExercisesForTraining(programTraining)).thenReturn(LiveDataUtil.getLive(oldChildren));

        ProgramTrainingSaver saver = new ProgramTrainingSaver(tree, repository);
        saver.save();

        verify(repository).getProgramExercisesForTraining(programTraining);
        verify(repository).updateProgramTraining(programTraining);
        verify(repository).updateProgramExercises(argThat(u -> {
            assertEquals(Lists.newArrayList(2L,3L), u.stream().map(Model::getId).sorted(Long::compare).collect(Collectors.toList()));
            return true;
        }));
        verify(repository).deleteProgramExercises(argThat(d -> d.iterator().next().getId() == 4L));
    }
}