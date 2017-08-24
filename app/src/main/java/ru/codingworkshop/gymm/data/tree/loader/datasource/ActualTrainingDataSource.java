package ru.codingworkshop.gymm.data.tree.loader.datasource;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.repository.ActualTrainingRepository;

/**
 * Created by Радик on 24.08.2017 as part of the Gymm project.
 */

public class ActualTrainingDataSource extends TreeDataSource<ActualTraining, ActualExercise, ActualSet> {

    public ActualTrainingDataSource(ActualTrainingRepository repository, long actualTrainingId) {
        setParent(repository.getActualTrainingById(actualTrainingId));
        setChildren(repository.getActualExercisesForActualTraining(actualTrainingId));
        setGrandchildren(repository.getActualSetsForActualTraining(actualTrainingId));
    }
}
