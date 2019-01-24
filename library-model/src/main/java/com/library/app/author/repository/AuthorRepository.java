package com.library.app.author.repository;

import com.library.app.author.model.Author;
import com.library.app.author.model.AuthorFilter;
import com.library.app.common.model.PaginatedData;
import com.library.app.common.repository.GenericRepository;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class AuthorRepository extends GenericRepository<Author> {

    @PersistenceContext
    public EntityManager entityManager;

    @Override
    protected Class<Author> getPersistentClass() {
        return Author.class;
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @SuppressWarnings("unchecked")
    public PaginatedData<Author> findByFilter(final AuthorFilter filter) {
        StringBuilder clause = new StringBuilder("WHERE e.id is not null");
        Map<String, Object> queryParameters = new HashMap<>();
        if (filter.getName() != null) {
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
        if (filter.hasPaginationData()) {
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
