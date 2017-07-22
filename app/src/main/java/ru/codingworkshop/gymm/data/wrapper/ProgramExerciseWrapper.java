package ru.codingworkshop.gymm.data.wrapper;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;

/**
 * Created by Радик on 13.07.2017.
 */

public class ProgramExerciseWrapper {
    private Exercise exercise;
    private List<ProgramSet> programSets;

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public List<ProgramSet> getProgramSets() {
        return programSets;
    }

    public void setProgramSets(List<ProgramSet> programSets) {
        this.programSets = programSets;
    }
}
