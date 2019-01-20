package com.library.app.common.utils.db;

import com.library.app.category.model.Category;
import org.junit.Ignore;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;

@Ignore
@Stateless
public class TestRepositoryEJB {

    @PersistenceContext
    private EntityManager em;

    private static final List<Class<?>> ENTITIES_TO_REMOVE = Collections.singletonList(Category.class);

    public void deleteAll() {
        for (Class<?> entityClass : ENTITIES_TO_REMOVE) {
            deleteAllForEntity(entityClass);
        }
    }

    @SuppressWarnings("unchecked")
    private void deleteAllForEntity(final Class<?> entityClass) {
        final List<Object> rows = em.createQuery("Select e From " + entityClass.getSimpleName() + " e").getResultList();
        for (final Object row : rows) {
            em.remove(row);
        }
    }

}
