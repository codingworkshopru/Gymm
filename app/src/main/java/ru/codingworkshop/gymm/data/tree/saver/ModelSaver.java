package ru.codingworkshop.gymm.data.tree.saver;

import android.support.annotation.NonNull;

import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ParentAdapter;
import ru.codingworkshop.gymm.db.GymmDatabase;

/**
 * Created by Radik on 28.11.2017.
 */

public class ModelSaver<T extends Model> implements Saver<T> {
    private ParentAdapter<T> parentAdapter;

    public ModelSaver(ParentAdapter<T> parentAdapter) {
        this.parentAdapter = parentAdapter;
    }

    @Override
    public void save(@NonNull T objectToSave) {
        if (GymmDatabase.isValidId(objectToSave)) {
            parentAdapter.updateParent(objectToSave);
        } else {
            parentAdapter.insertParent(objectToSave);
        }
    }
}
