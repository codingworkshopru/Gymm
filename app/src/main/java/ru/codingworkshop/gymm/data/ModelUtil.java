package ru.codingworkshop.gymm.data;

import com.google.common.base.Preconditions;

import java.util.List;

import io.requery.Persistable;
import io.requery.meta.Attribute;
import io.requery.meta.Type;
import io.requery.proxy.EntityProxy;
import io.requery.proxy.PropertyState;
import io.requery.sql.EntityDataStore;

/**
 * Created by Радик on 04.05.2017.
 */

public final class ModelUtil {

    @SuppressWarnings("unchecked")
    private static <E> Type<E> extractType(E entity) {
        Type<E> result = null;
        try {
            result = (Type<E>) entity.getClass().getDeclaredField("$TYPE").get(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Preconditions.checkNotNull(result, "Couldn't extract type from entity");
    }

    private static <E> boolean isModified(E entity, boolean association) {
        Type<E> type = extractType(entity);

        EntityProxy<E> proxy = type.getProxyProvider().apply(entity);
        for (Attribute<E, ?> attr : type.getAttributes()) {
            if (!association || attr.isAssociation())
                if (proxy.getState(attr) == PropertyState.MODIFIED)
                    return true;
        }

        return false;
    }

    public static <E> boolean isEntityModified(E entity) {
        return isModified(entity, false);
    }

    public static <E> boolean areAssociationsModified(E entity) {
        return isModified(entity, true);
    }

    public static <E> boolean isAttributeModified(E entity, Attribute<E, ?> attribute) {
        Type<E> type = extractType(entity);
        EntityProxy<E> proxy = type.getProxyProvider().apply(entity);
        return proxy.getState(attribute) == PropertyState.MODIFIED;
    }

    public static <E extends Persistable> void refreshAll(E entity, EntityDataStore<Persistable> data) {
        Type<E> type = extractType(entity);
        EntityProxy<E> proxy = type.getProxyProvider().apply(entity);

        for (Attribute<E, ?> attribute : type.getAttributes()) {
            if (attribute.isAssociation() && proxy.getState(attribute) == PropertyState.MODIFIED) {
                Object field = proxy.get(attribute);
                if (field instanceof List) {
                    List<E> entityList = (List<E>) field;
                    for (E childEntity : entityList)
                        refreshAll(childEntity, data);
                } else {
                    refreshAll((Persistable) field, data);
                }
            }
        }

        data.refreshAll(entity);
    }
}
