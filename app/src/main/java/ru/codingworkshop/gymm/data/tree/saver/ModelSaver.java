package ru.codingworkshop.gymm.data.tree.saver;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.db.GymmDatabase;

/**
 * Created by Радик on 18.08.2017 as part of the Gymm project.
 */

public class ModelSaver<P extends Model> implements Saver {
    private P model;
    private ModelSaverCallback<P> callback;

    public interface ModelSaverCallback<P> {
        void updateParent(P parent);
        void insertParent(P parent);
    }

    public ModelSaver(@NonNull P model, @NonNull ModelSaverCallback<P> callback) {
        this.model = Preconditions.checkNotNull(model);
        this.callback = Preconditions.checkNotNull(callback);
    }

    @Override
    public void save() {
        if (GymmDatabase.isValidId(model)) {
            callback.updateParent(model);
        } else {
            callback.insertParent(model);
        }
    }
}
