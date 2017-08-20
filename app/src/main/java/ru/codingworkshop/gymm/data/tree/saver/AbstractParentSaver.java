package ru.codingworkshop.gymm.data.tree.saver;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.db.GymmDatabase;

/**
 * Created by Радик on 18.08.2017 as part of the Gymm project.
 */

public abstract class AbstractParentSaver<P extends Model> implements ParentSaver<P> {
    private P parent;

    public AbstractParentSaver(@NonNull P parent) {
        this.parent = Preconditions.checkNotNull(parent);
    }

    @Override
    public void save() {
        if (GymmDatabase.isValidId(parent)) {
            updateParent(parent);
        } else {
            insertParent(parent);
        }
    }
}
