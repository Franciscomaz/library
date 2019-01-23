package com.library.app.author.repository;

import com.library.app.author.model.Author;
import com.library.app.author.model.AuthorFilter;
import com.library.app.common.model.PaginatedData;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @SuppressWarnings("unchecked")
    public PaginatedData<Author> findByFilter(final AuthorFilter filter) {
        StringBuilder clause = new StringBuilder("WHERE e.id is not null");
        Map<String, Object> queryParameters = new HashMap<>();
        if(filter.getName() != null) {
            clause.append(" And UPPER(e.name) Like UPPER(:name)");
            queryParameters.put("name", "%" + filter.getName() + "%");
        }

        StringBuilder clauseSort = new StringBuilder();
        if (filter.hasOrderField()) {
            clauseSort.append("Order by e." + filter.getPaginationData().getOrderField());
            clauseSort.append(filter.getPaginationData().isAscending() ? " ASC" : " DESC");
        } else {
            clauseSort.append("Order by e.name ASC");
        }

        Query queryAuthor = entityManager.createQuery("select e From Author e " + clause.toString() + " " + clauseSort.toString());
        applyQueryParametersQuery(queryParameters, queryAuthor);
        if(filter.hasPaginationData()) {
            queryAuthor.setFirstResult(filter.getPaginationData().getFirstResult());
            queryAuthor.setMaxResults(filter.getPaginationData().getMaxResults());
        }

        List<Author> authors = queryAuthor.getResultList();

        Query queryCount = entityManager.createQuery("select Count(e) from Author e " + clause.toString());
        applyQueryParametersQuery(queryParameters, queryCount);
        Integer total = ((Long) queryCount.getSingleResult()).intValue();

        return new PaginatedData<>(total, authors);
    }

    private void applyQueryParametersQuery(Map<String, Object> queryParameters, Query query) {
        for (Map.Entry<String, Object> entryMap : queryParameters.entrySet()) {
            query.setParameter(entryMap.getKey(), entryMap.getValue());
        }
    }
}
