package com.library.app.category.repository;

import com.library.app.category.model.Category;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Objects;

@Stateless
public class CategoryRepository {

    @PersistenceContext
    public EntityManager entityManager;

    public Category add(final Category category) {
        entityManager.persist(category);
        return category;
    }

    public Category findBy(long id) {
        return entityManager.find(Category.class, id);
    }

    public List<Category> findAll() {
        return entityManager
                .createQuery("Select e From Category e")
                .getResultList();
    }

    public Category update(Category category) {
        return entityManager.merge(category);
    }

    public Category remove(Category category) {
        entityManager.remove(category);
        return category;
    }

    public boolean alreadyExists(Category category) {
        StringBuilder builder = new StringBuilder("Select 1 From Category e where e.name = :name");
        if(Objects.nonNull(category.getId())) {
            builder.append(" and id != :id");
        }

        final Query query = entityManager.createQuery(builder.toString());
        query.setParameter("name", category.getName());
        if(Objects.nonNull(category.getId())) {
            query.setParameter("id", category.getId());
        }

        return query.setMaxResults(1).getResultList().size() > 0;
    }

    public boolean existsById(Long id) {
        return entityManager.createQuery("Select 1 From Category Where id = :id")
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultList()
                .size() > 0;
    }
}
