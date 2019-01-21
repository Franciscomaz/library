package com.library.app.author.repository;

import com.library.app.author.model.Author;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class AuthorRepository {

    @PersistenceContext
    EntityManager entityManager;


    public Author add(final Author author) {
        entityManager.persist(author);
        return author;
    }

    public Author findBy(final Long id) {
        return entityManager.find(Author.class, id);
    }

    public Author update(final Author author) {
        return entityManager.merge(author);
    }

    public void delete(final Author author) {
        entityManager.remove(author);
    }

    public boolean existsBy(final Long id) {
        return entityManager.createQuery("select 1 from Author author where author.id = :id")
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultList()
                .size() > 0;
    }
}
