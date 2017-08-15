package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.lifecycle.LiveData;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingNode;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Радик on 14.08.2017 as part of the Gymm project.
 */

public class ProgramTrainingLoader {
    private ProgramTrainingRepository repository;

    public ProgramTrainingLoader(ProgramTrainingRepository repository) {
        this.repository = repository;
    }

    public LiveData<Boolean> loadToNode(ProgramTrainingNode node, long programTrainingId) {
        SetAndRemove setAndRemove = new SetAndRemove(2);

        final LiveData<ProgramTraining> programTrainingById = repository.getProgramTrainingById(programTrainingId);
        final LiveData<List<ProgramExercise>> programExercisesForTraining = repository.getProgramExercisesForTraining(programTrainingId);

        setAndRemove.ok(programTrainingById, node::setParent);
        setAndRemove.ok(programExercisesForTraining, node::setChildren);

        return setAndRemove.getLoaded();
    }
}
