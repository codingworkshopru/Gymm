package ru.codingworkshop.gymm.data.tree.node;

import org.junit.Before;
import org.junit.Test;

import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.util.ModelsFixture;

import static org.junit.Assert.assertEquals;

/**
 * Created by Радик on 22.08.2017 as part of the Gymm project.
 */
public class ActualTrainingTreeTest {
    private ActualTrainingTree tree;

    @Before
    public void setUp() throws Exception {
        tree = new ActualTrainingTree();
    }

    @Test
    public void programTrainingAccessors() throws Exception {
        final ProgramTraining programTraining = ModelsFixture.createProgramTraining(1L, "foo");
        tree.setProgramTraining(programTraining);
        assertEquals(programTraining, tree.getProgramTraining());
    }


}