package ru.codingworkshop.gymm.data.tree.node;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.tree.holder.SimpleChildrenHolder;

/**
 * Created by Радик on 18.08.2017 as part of the Gymm project.
 */

public class ActualExerciseNode extends BaseNode<ActualExercise, ActualSet> {
    private ProgramExerciseNode programExerciseNode;

    public ActualExerciseNode() {
        super(new SimpleChildrenHolder<>());
    }

    public ActualExerciseNode(ActualExercise parent) {
        this();
        setParent(parent);

    }

    public ProgramExerciseNode getProgramExerciseNode() {
        return programExerciseNode;
    }

    public void setProgramExerciseNode(ProgramExerciseNode programExerciseNode) {
        this.programExerciseNode = programExerciseNode;
    }
}
