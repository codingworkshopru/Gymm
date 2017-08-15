package ru.codingworkshop.gymm.data.tree.node;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by Радик on 14.08.2017 as part of the Gymm project.
 */

public class ProgramTrainingNodesNode extends ProgramTrainingSortableRestoreChildrenNode {
    private List<ProgramExerciseNode> getChildrenNodes() {
        return Lists.transform(getChildren(), ch -> (ProgramExerciseNode) ch);
    }
}
