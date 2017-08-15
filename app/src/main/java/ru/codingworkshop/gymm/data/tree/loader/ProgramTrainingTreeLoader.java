package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.List;
import java.util.Map;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseSortableRestoreChildrenNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingNode;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Радик on 14.08.2017 as part of the Gymm project.
 */

public class ProgramTrainingTreeLoader {
    private ProgramTrainingRepository repository;
    private ExercisesRepository exercisesRepository;

    private List<ProgramExercise> programExercises;
    private Multimap<Long, ProgramSet> programSets;
    private Map<Long, Exercise> exercises;

    public ProgramTrainingTreeLoader(ProgramTrainingRepository repository, ExercisesRepository exercisesRepository) {
        this.repository = repository;
        this.exercisesRepository = exercisesRepository;
    }

    public LiveData<Boolean> loadToNode(ProgramTrainingNode node, long programTrainingId) {
        LiveData<ProgramTraining> programTrainingLiveData = repository.getProgramTrainingById(programTrainingId);
        LiveData<List<ProgramExercise>> programExercisesLiveData = repository.getProgramExercisesForTraining(programTrainingId);
        LiveData<List<ProgramSet>> programSetsLiveData = repository.getProgramSetsForTraining(programTrainingId);
        LiveData<List<Exercise>> exercisesLiveData = exercisesRepository.getExercisesForProgramTraining(programTrainingId);

        SetAndRemove setAndRemove = new SetAndRemove(4);

        setAndRemove.ok(programTrainingLiveData, node::setParent);
        setAndRemove.ok(programExercisesLiveData, this::setProgramExercises);
        setAndRemove.ok(programSetsLiveData, this::setProgramSets);
        setAndRemove.ok(exercisesLiveData, this::setExercises);

        LiveData<Boolean> loaded = setAndRemove.getLoaded();
        loaded.observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isLoaded) {
                if (isLoaded != null && isLoaded) {
                    buildTree(node);
                    loaded.removeObserver(this);
                }
            }
        });

        return loaded;
    }

    private void setProgramExercises(List<ProgramExercise> programExercises) {
        this.programExercises = programExercises;
    }

    private void setProgramSets(List<ProgramSet> programSets) {
        this.programSets = Multimaps.index(programSets, ProgramSet::getProgramExerciseId);
    }

    private void setExercises(List<Exercise> exercises) {
        this.exercises = Maps.uniqueIndex(exercises, Exercise::getId);
    }

    private void buildTree(ProgramTrainingNode tree) {
        List<ProgramExerciseNode> exerciseNodes = Lists.newArrayListWithCapacity(programExercises.size());
        for (ProgramExercise exercise : programExercises) {
            ProgramExerciseNode node = getProgramExerciseNode(exercise);
            exerciseNodes.add(node);
        }
        tree.setChildren(exerciseNodes);
    }

    @NonNull
    private ProgramExerciseNode getProgramExerciseNode(ProgramExercise exercise) {
        ProgramExerciseNode node = new ProgramExerciseSortableRestoreChildrenNode();
        node.setParent(exercise);
        node.setChildren(Lists.newArrayList(programSets.get(exercise.getId())));
        node.setExercise(exercises.get(exercise.getExerciseId()));
        return node;
    }
}
