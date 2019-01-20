package com.library.app.common.utils.db;

import javax.persistence.EntityManager;

public class DBCommandTransactionalExecutor {
    private EntityManager entityManager;

    public DBCommandTransactionalExecutor(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public <T> T execute(DBCommand<T> command) {
        try {
            entityManager.getTransaction().begin();
            T obejct = command.execute();
            entityManager.getTransaction().commit();
            entityManager.clear();
            return obejct;
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new IllegalStateException(e);
        }
    }
}
