package ru.codingworkshop.gymm.data.tree.saver;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Completable;
import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.SingleModelAlterAdapter;

import static ru.codingworkshop.gymm.db.GymmDatabase.isValidId;

/**
 * Created by Radik on 28.11.2017.
 */

public class ModelSaver<T extends Model> implements Saver<T> {
    private SingleModelAlterAdapter<T> alterAdapter;

    public ModelSaver(SingleModelAlterAdapter<T> alterAdapter) {
        this.alterAdapter = alterAdapter;
    }

    @Nullable
    @Override
    public Completable save(@NonNull T objectToSave) {
        if (isValidId(objectToSave)) {
            alterAdapter.update(objectToSave);
        } else {
            alterAdapter.insert(objectToSave);
        }

        return null;
    }
}
