package ru.codingworkshop.gymm.data.wrapper;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramSetEntity;
import ru.codingworkshop.gymm.data.model.Exercise;

/**
 * Created by Радик on 13.07.2017.
 */

public class ProgramExerciseWrapper {
    private Exercise exercise;
    private List<ProgramSetEntity> programSets;

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public List<ProgramSetEntity> getProgramSets() {
        return programSets;
    }

    public void setProgramSets(List<ProgramSetEntity> programSets) {
        this.programSets = programSets;
    }
}
