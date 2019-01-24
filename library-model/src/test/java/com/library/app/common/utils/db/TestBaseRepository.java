package com.library.app.common.utils.db;

import org.junit.Ignore;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@Ignore
public class TestBaseRepository {

    private EntityManagerFactory entityManagerFactory;
    protected DBCommandTransactionalExecutor dbCommandTransactionalExecutor;
    protected EntityManager entityManager;

    public void initTestCase() {
        entityManagerFactory = Persistence.createEntityManagerFactory("libraryPU");
        entityManager = entityManagerFactory.createEntityManager();

        dbCommandTransactionalExecutor = new DBCommandTransactionalExecutor(entityManager);
    }

    public void closeEntityManager() {
        entityManager.close();
        entityManagerFactory.close();
    }
}
