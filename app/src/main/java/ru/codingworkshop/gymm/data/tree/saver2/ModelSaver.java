package ru.codingworkshop.gymm.data.tree.saver2;

import android.support.annotation.NonNull;

import com.google.common.base.Equivalence;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ParentAdapter;
import ru.codingworkshop.gymm.data.util.BiPredicate;
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
    public void save(T objectToSave) {
        if (GymmDatabase.isValidId(objectToSave)) {
            parentAdapter.updateParent(objectToSave);
        } else {
            parentAdapter.insertParent(objectToSave);
        }
    }
}
