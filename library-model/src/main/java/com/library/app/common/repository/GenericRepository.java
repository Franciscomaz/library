package com.library.app.common.repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Objects;

public abstract class GenericRepository<T> {

    protected abstract Class<T> getPersistentClass();

    protected abstract EntityManager getEntityManager();

    public T add(final T entity) {
        getEntityManager().persist(entity);
        return entity;
    }

    public T findBy(final Long id) {
        if (id == null) {
            return null;
        }
        return getEntityManager().find(getPersistentClass(), id);
    }

    public T update(T entity) {
        return getEntityManager().merge(entity);
    }

    public T remove(T entity) {
        getEntityManager().remove(entity);
        return entity;
    }

    public List<T> findAll() {
        return getEntityManager()
                .createQuery("Select e From " + getPersistentClass().getSimpleName() + " e")
                .getResultList();
    }

    public boolean existsById(Long id) {
        return getEntityManager().createQuery("Select 1 From " + getPersistentClass().getSimpleName() + " e Where e.id = :id")
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultList()
                .size() > 0;
    }

    public boolean alreadyExists(Long entityId, String propertyName, String propertyValue) {
        StringBuilder builder = new StringBuilder("Select 1 From " + getPersistentClass().getSimpleName() + " e where e.name = :name");
        if (Objects.nonNull(entityId)) {
            builder.append(" and id != :id");
        }

        final Query query = getEntityManager().createQuery(builder.toString());
        query.setParameter(propertyName, propertyValue);
        if (Objects.nonNull(entityId)) {
            query.setParameter("id", entityId);
        }

        return query.setMaxResults(1).getResultList().size() > 0;
    }
}
