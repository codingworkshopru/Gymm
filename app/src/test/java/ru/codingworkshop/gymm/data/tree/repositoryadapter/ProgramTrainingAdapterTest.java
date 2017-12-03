package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collection;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;
import ru.codingworkshop.gymm.util.Models;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;

/**
 * Created by Radik on 11.11.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProgramTrainingAdapterTest {
    @Mock private ProgramTrainingRepository programTrainingRepository;
    @Mock private ExercisesRepository exercisesRepository;
    @InjectMocks private ProgramTrainingAdapter adapter;

    @Test
    public void getExercises() throws Exception {
        adapter.getExercises(1L);
        verify(exercisesRepository).getExercisesForProgramTraining(1L);
    }

    @Test
    public void getDrafting() throws Exception {
        adapter.getDrafting();
        verify(programTrainingRepository).getDraftingProgramTraining();
    }

    @Test
    public void getProgramTrainingByName() throws Exception {
        adapter.getProgramTrainingByName("foo");
        verify(programTrainingRepository).getProgramTrainingByName("foo");
    }

    @Test
    public void getProgramSetsForExercise() throws Exception {
        adapter.getProgramSetsForExercise(2L);
        verify(programTrainingRepository).getProgramSetsForExercise(2L);
    }

    @Test
    public void getParent() throws Exception {
        adapter.getParent(1L);
        verify(programTrainingRepository).getProgramTrainingById(1L);
    }

    @Test
    public void updateParent() throws Exception {
        ProgramTraining training = Models.createProgramTraining(1L, "foo");
        adapter.updateParent(training);
        verify(programTrainingRepository).updateProgramTraining(training);
    }

    @Test
    public void insertParent() throws Exception {
        ProgramTraining training = Models.createProgramTraining(1L, "foo");
        adapter.insertParent(training);
        verify(programTrainingRepository).insertProgramTraining(training);
    }

    @Test
    public void deleteParent() throws Exception {
        ProgramTraining training = Models.createProgramTraining(1L, "foo");
        adapter.deleteParent(training);
        verify(programTrainingRepository).deleteProgramTraining(training);
    }

    @Test
    public void getChildren() throws Exception {
        adapter.getChildren(1L);
        verify(programTrainingRepository).getProgramExercisesForTraining(1L);
    }

    @Test
    public void insertChildren() throws Exception {
        Collection<ProgramExercise> programExercises = Models.createProgramExercises(1);
        adapter.insertChildren(programExercises);
        verify(programTrainingRepository).insertProgramExercises(programExercises);
    }

    @Test
    public void updateChildren() throws Exception {
        Collection<ProgramExercise> programExercises = Models.createProgramExercises(1);
        adapter.updateChildren(programExercises);
        verify(programTrainingRepository).updateProgramExercises(programExercises);
    }

    @Test
    public void deleteChildren() throws Exception {
        Collection<ProgramExercise> programExercises = Models.createProgramExercises(1);
        adapter.deleteChildren(programExercises);
        verify(programTrainingRepository).deleteProgramExercises(programExercises);
    }

    @Test
    public void getGrandchildren()  {
        adapter.getGrandchildren(1L);
        verify(programTrainingRepository).getProgramSetsForTraining(1L);
    }

    @Test
    public void insertGrandchildren() throws Exception {
        List<ProgramSet> programSets = Models.createProgramSets(2L, 1);
        adapter.insertGrandchildren(programSets);
        verify(programTrainingRepository).insertProgramSets(programSets);
    }

    @Test
    public void updateGrandchildren() throws Exception {
        List<ProgramSet> programSets = Models.createProgramSets(2L, 1);
        adapter.updateGrandchildren(programSets);
        verify(programTrainingRepository).updateProgramSets(programSets);
    }

    @Test
    public void deleteGrandchildren() throws Exception {
        List<ProgramSet> programSets = Models.createProgramSets(2L, 1);
        adapter.deleteGrandchildren(programSets);
        verify(programTrainingRepository).deleteProgramSets(programSets);
    }
}