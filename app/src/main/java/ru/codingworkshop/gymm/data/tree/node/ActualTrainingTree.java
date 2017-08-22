package ru.codingworkshop.gymm.data.tree.node;

import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.holder.SimpleChildrenHolder;

/**
 * Created by Радик on 18.08.2017 as part of the Gymm project.
 */

public class ActualTrainingTree extends BaseNode<ActualTraining, ActualExerciseNode> {

    private ProgramTraining programTraining;

    public ActualTrainingTree() {
        super(new SimpleChildrenHolder<>());
    }

    public ProgramTraining getProgramTraining() {
        return programTraining;
    }

    public void setProgramTraining(ProgramTraining programTraining) {
        this.programTraining = programTraining;
    }
}
