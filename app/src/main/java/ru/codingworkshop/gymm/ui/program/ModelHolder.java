package ru.codingworkshop.gymm.ui.program;

import java.util.List;

import io.requery.Persistable;
import io.requery.query.Condition;
import io.requery.sql.EntityDataStore;
import ru.codingworkshop.gymm.data.ModelUtil;
import ru.codingworkshop.gymm.data.model.Draftable;
import ru.codingworkshop.gymm.data.model.Orderable;
import ru.codingworkshop.gymm.data.model.ProgramExerciseEntity;
import ru.codingworkshop.gymm.data.model.ProgramSet;
import ru.codingworkshop.gymm.data.model.ProgramSetEntity;

/**
 * Created by Радик on 05.05.2017.
 */

public class ModelHolder<T extends Persistable & Draftable, C extends Persistable & Orderable & Draftable> {
    private T model;
    private EntityDataStore<Persistable> db;
    private EntityAdapter<T, C> adapter;

    public ModelHolder(EntityDataStore<Persistable> db) {
        this.db = db;
    }

    public void setModel(T m) {
        model = m;
    }

    public List<C> getList() {
        return adapter.getChildren(model);
    }

    public void addNewChild(C child) {
        int index = getList().size();
        if (!child.isDrafting())
            child.setDrafting(true);
        child.setSortOrder(index);
        adapter.setParent(child, model);
        db.upsert(child);
        getList().add(index, child);
    }

    public void replaceChild(C child) {
        getList().set(child.getSortOrder(), child);
    }

    public void select(long id) {
        setModel(adapter.select(db, id, false));
    }

    public void selectWithDrafting(long id) {
        setModel(adapter.select(db, id, true));
    }

    public T createNewModel() {
        T m = adapter.create();
        m.setDrafting(true);
        db.insert(m);
        setModel(m);
        return m;
    }

    public void saveAllChanges() {
        if (model.isDrafting())
            model.setDrafting(false);
        db.update(model);
        adapter.deleteUnneededChildren(model, db);
    }

    public void undoAllChanges() {
        if (model.isDrafting()) {
            db.delete(model);
        } else if (ModelUtil.areAssociationsModified(model)) {
            ModelUtil.refreshAll(model, db);
        }
    }

    public boolean isModified() {
        return ModelUtil.isEntityModified(model);
    }

    private interface EntityAdapter<T extends Persistable, C extends Persistable> {
        T create();
        void setParent(C model, T parent);
        List<C> getChildren(T model);
        T select(EntityDataStore<Persistable> db, long id, boolean withDrafting);
        void deleteUnneededChildren(T model, EntityDataStore<Persistable> db);
    }

    public static final class ProgramExerciseAdapter implements EntityAdapter<ProgramExerciseEntity, ProgramSet> {
        @Override
        public ProgramExerciseEntity create() {
            return new ProgramExerciseEntity();
        }

        @Override
        public void setParent(ProgramSet model, ProgramExerciseEntity parent) {
            model.setProgramExercise(parent);
        }

        @Override
        public List<ProgramSet> getChildren(ProgramExerciseEntity model) {
            return model.getSets();
        }

        @Override
        public ProgramExerciseEntity select(EntityDataStore<Persistable> db, long id, boolean withDrafting) {
            Condition<?, ?> condition = withDrafting ? ProgramExerciseEntity.ID.eq(id) : ProgramExerciseEntity.ID.eq(id).and(ProgramExerciseEntity.DRAFTING.eq(false));
            return db.select(ProgramExerciseEntity.class)
                    .where(condition)
                    .get()
                    .first();
        }

        @Override
        public void deleteUnneededChildren(ProgramExerciseEntity model, EntityDataStore<Persistable> db) {
            try {
                db.delete(ProgramSetEntity.class)
                        .where(ProgramSetEntity.DRAFTING.eq(true).and(ProgramSetEntity.PROGRAM_EXERCISE.eq(model)))
                        .get()
                        .call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}