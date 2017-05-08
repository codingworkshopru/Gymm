package ru.codingworkshop.gymm.ui.program;

import android.os.Bundle;
import android.util.Log;

import java.util.Collection;
import java.util.List;

import io.requery.Persistable;
import io.requery.meta.QueryAttribute;
import io.requery.proxy.CollectionChanges;
import io.requery.sql.EntityDataStore;
import io.requery.util.ObservableList;
import ru.codingworkshop.gymm.data.ModelUtil;
import ru.codingworkshop.gymm.data.model.Draftable;
import ru.codingworkshop.gymm.data.model.Orderable;
import ru.codingworkshop.gymm.data.model.ProgramExercise;
import ru.codingworkshop.gymm.data.model.ProgramExerciseEntity;
import ru.codingworkshop.gymm.data.model.ProgramSet;
import ru.codingworkshop.gymm.data.model.ProgramSetEntity;
import ru.codingworkshop.gymm.data.model.ProgramTraining;
import ru.codingworkshop.gymm.data.model.ProgramTrainingEntity;

/**
 * Created by Радик on 05.05.2017.
 */

public class ModelHolder<T extends Persistable & Draftable, C extends Persistable & Orderable & Draftable> {
    private T model;
    private EntityDataStore<Persistable> db;
    private EntityAdapter<T, C> adapter;

    private static final String TAG = ModelHolder.class.getSimpleName();

    public ModelHolder(EntityDataStore<Persistable> db, EntityAdapter<T, C> adapter) {
        this.db = db;
        this.adapter = adapter;
    }

    public static <T extends Persistable & Draftable, C extends Persistable & Orderable & Draftable>
    ModelHolder<T, C> newInstance(EntityDataStore<Persistable> db, EntityAdapter<T, C> adapter, Bundle args, String key)
    {
        ModelHolder<T, C> instance = new ModelHolder<>(db, adapter);

        if (args != null && args.containsKey(key)) {
            long id = args.getLong(key);
            instance.select(id);
        } else {
            instance.createNewModel();
        }

        return instance;
    }

    private void l(String message) {
        Log.d(TAG, message);
    }

    public void setModel(T m) {
        l("new model set");
        model = m;
    }

    public T getModel() {
        return model;
    }

    public List<C> getChildren() {
        return adapter.getChildren(model);
    }

    public void addNewChild(C child) {
        l("add new child");
        adapter.setParent(child, model);
        db.upsert(child);
    }

    public void replaceChild(C child) {
        l("replace child");
        db.update(child);
    }

    private void select(long id ) {
        l("select");
        setModel(
                db.select(adapter.getEntityClass())
                        .where(adapter.idAttribute().eq(id))
                        .get()
                        .first()
        );
    }

    private void deleteUnattachedChildren() {
        try {
            db.delete(adapter.getChildClass())
                    .where(adapter.childParentAttribute().isNull())
                    .get()
                    .call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public T createNewModel() {
        l("create new model");
        T m = adapter.create();
        m.setDrafting(true);
        db.upsert(m);
        setModel(m);
        return m;
    }

    public void saveAllChanges() {
        l("save all changes");
        if (model.isDrafting())
            model.setDrafting(false);
        db.update(model);
        deleteUnattachedChildren();
    }

    public void undoAllChanges() {
        l("undo all changes");
        if (model.isDrafting()) {
            l("\tdelete model");
            db.delete(model);
        } else if (ModelUtil.areAssociationsModified(model)) {
            l("\trefresh model");
            ObservableList list = (ObservableList) getChildren();
            CollectionChanges observer = (CollectionChanges) list.observer();
            Collection toDelete = observer.addedElements();
            ModelUtil.refreshAll(model, db);
            db.delete(toDelete);
        }
    }

    public boolean isModified() {
        return ModelUtil.isEntityModified(model);
    }

    private interface EntityAdapter<T extends Persistable, C extends Persistable> {
        T create();
        void setParent(C model, T parent);
        List<C> getChildren(T model);
        QueryAttribute<?, Long> idAttribute();
        QueryAttribute<?, T> childParentAttribute();
        Class<T> getEntityClass();
        Class<C> getChildClass();
    }

    public static final class ProgramExerciseAdapter implements EntityAdapter<ProgramExercise, ProgramSet> {
        @Override
        public ProgramExercise create() {
            return new ProgramExerciseEntity();
        }

        @Override
        public void setParent(ProgramSet model, ProgramExercise parent) {
            model.setProgramExercise(parent);
        }

        @Override
        public List<ProgramSet> getChildren(ProgramExercise model) {
            return model.getSets();
        }

        @Override
        public QueryAttribute<?, Long> idAttribute() {
            return ProgramExerciseEntity.ID;
        }

        @Override
        public QueryAttribute<?, ProgramExercise> childParentAttribute() {
            return ProgramSetEntity.PROGRAM_EXERCISE;
        }

        @Override
        public Class<ProgramExercise> getEntityClass() {
            return ProgramExercise.class;
        }

        @Override
        public Class<ProgramSet> getChildClass() {
            return ProgramSet.class;
        }
    }

    public static final class ProgramTrainingAdapter implements EntityAdapter<ProgramTraining, ProgramExercise> {

        @Override
        public ProgramTraining create() {
            return new ProgramTrainingEntity();
        }

        @Override
        public void setParent(ProgramExercise model, ProgramTraining parent) {
            model.setProgramTraining(parent);
        }

        @Override
        public List<ProgramExercise> getChildren(ProgramTraining model) {
            return model.getExercises();
        }

        @Override
        public QueryAttribute<?, Long> idAttribute() {
            return ProgramTrainingEntity.ID;
        }

        @Override
        public QueryAttribute<?, ProgramTraining> childParentAttribute() {
            return ProgramExerciseEntity.PROGRAM_TRAINING;
        }

        @Override
        public Class<ProgramTraining> getEntityClass() {
            return ProgramTraining.class;
        }

        @Override
        public Class<ProgramExercise> getChildClass() {
            return ProgramExercise.class;
        }
    }
}