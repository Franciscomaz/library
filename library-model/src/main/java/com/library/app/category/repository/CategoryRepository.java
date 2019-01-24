package com.library.app.category.repository;

import com.library.app.category.model.Category;
import com.library.app.common.repository.GenericRepository;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class CategoryRepository extends GenericRepository<Category> {

    @PersistenceContext
    public EntityManager entityManager;

    @Override
    protected Class<Category> getPersistentClass() {
        return Category.class;
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public boolean alreadyExists(Category category) {
        return alreadyExists(category.getId(), "name", category.getName());
    }
}
