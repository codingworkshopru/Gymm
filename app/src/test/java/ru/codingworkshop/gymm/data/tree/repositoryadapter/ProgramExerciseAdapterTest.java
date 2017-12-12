package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.Models;

import static org.mockito.Mockito.verify;

/**
 * Created by Radik on 09.11.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProgramExerciseAdapterTest {

    @Mock private ProgramTrainingRepository programTrainingRepository;
    @Mock private ExercisesRepository exercisesRepository;
    @InjectMocks private ProgramExerciseAdapter programExerciseAdapter;

    @Test
    public void getExerciseTest() throws Exception {
        programExerciseAdapter.getExercise(2L);
        verify(exercisesRepository).getExerciseForProgramExercise(2L);
    }

    @Test
    public void getParentTest() throws Exception {
        programExerciseAdapter.getParent(2L);
        verify(programTrainingRepository).getProgramExerciseById(2L);
    }

    @Test
    public void getChildrenTest() throws Exception {
        programExerciseAdapter.getChildren(2L);
        verify(programTrainingRepository).getProgramSetsForExercise(2L);
    }

    @Test
    public void updateParentTest() throws Exception {
        ProgramExercise programExercise = Models.createProgramExercise(2L, 1L, 100L);
        programExerciseAdapter.updateParent(programExercise);
        verify(programTrainingRepository).updateProgramExercise(programExercise);
    }

    @Test
    public void insertParentTest() throws Exception {
        ProgramExercise programExercise = Models.createProgramExercise(2L, 1L, 100L);
        programExerciseAdapter.insertParent(programExercise);
        verify(programTrainingRepository).insertProgramExercise(programExercise);
    }

    @Test
    public void deleteParentTest() throws Exception {
        ProgramExercise programExercise = Models.createProgramExercise(2L, 1L, 100L);
        programExerciseAdapter.deleteParent(programExercise);
        verify(programTrainingRepository).deleteProgramExercise(programExercise);
    }

    @Test
    public void insertChildren() throws Exception {
        List<ProgramSet> programSets = Models.createProgramSets(2L, 1);
        programExerciseAdapter.insertChildren(programSets);
        verify(programTrainingRepository).insertProgramSets(programSets);
    }

    @Test
    public void updateChildren() throws Exception {
        List<ProgramSet> programSets = Models.createProgramSets(2L, 1);
        programExerciseAdapter.updateChildren(programSets);
        verify(programTrainingRepository).updateProgramSets(programSets);
    }

    @Test
    public void deleteChildren() throws Exception {
        List<ProgramSet> programSets = Models.createProgramSets(2L, 1);
        programExerciseAdapter.deleteChildren(programSets);
        verify(programTrainingRepository).deleteProgramSets(programSets);
    }
}
