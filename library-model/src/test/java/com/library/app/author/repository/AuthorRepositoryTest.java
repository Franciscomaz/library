package com.library.app.author.repository;

import com.library.app.author.model.Author;
import com.library.app.author.model.AuthorFilter;
import com.library.app.common.model.PaginatedData;
import com.library.app.common.model.filter.PaginationData;
import com.library.app.common.utils.author.AuthorFactory;
import com.library.app.common.utils.db.DBCommandTransactionalExecutor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Matchers.isNotNull;

public class AuthorRepositoryTest {
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private AuthorRepository authorRepository;
    private DBCommandTransactionalExecutor dbCommandTransactionalExecutor;

    @Before
    public void initTestCase() {
        entityManagerFactory = Persistence.createEntityManagerFactory("libraryPU");
        entityManager = entityManagerFactory.createEntityManager();

        dbCommandTransactionalExecutor = new DBCommandTransactionalExecutor(entityManager);

        authorRepository = new AuthorRepository();
        authorRepository.entityManager = entityManager;
    }

    @After
    public void closeEntityManager() {
        entityManager.close();
        entityManagerFactory.close();
    }

    @Test
    public void shouldAddAndFindAuthor() {
        final Long addedAuthorId = dbCommandTransactionalExecutor.execute(() -> authorRepository.add(AuthorFactory.createMartinFowler()).getId());

        final Author author = authorRepository.findBy(addedAuthorId);
        Assert.assertThat(author, is(notNullValue()));
        Assert.assertThat(author.getId(), is(addedAuthorId));
        Assert.assertThat(author.getName(), is(AuthorFactory.createMartinFowler().getName()));
    }

    @Test
    public void shouldNotFindAuthor() {
        final Author foundAuthor = authorRepository.findBy(1L);
        Assert.assertThat(foundAuthor, is(nullValue()));
    }

    @Test
    public void shouldUpdateAuthor() {
        final Long addedAuthorId = dbCommandTransactionalExecutor.execute(() -> authorRepository.add(AuthorFactory.createMartinFowler()).getId());
        Assert.assertThat(addedAuthorId, is(notNullValue()));

        final Author foundAuthor = authorRepository.findBy(addedAuthorId);
        Assert.assertThat(foundAuthor, is(notNullValue()));

        final String newAuthorName = AuthorFactory.createUncleBob().getName();
        foundAuthor.setName(newAuthorName);
        dbCommandTransactionalExecutor.execute(() -> {
            authorRepository.update(foundAuthor);
            return null;
        });

        final Author updatedAuthor = authorRepository.findBy(addedAuthorId);
        Assert.assertThat(updatedAuthor.getName(), is(newAuthorName));
    }

    @Test
    public void shouldDeleteAuthor() {
        final Long addedAuthorId = dbCommandTransactionalExecutor.execute(() -> authorRepository.add(AuthorFactory.createMartinFowler()).getId());
        final Author addedAuthor = authorRepository.findBy(addedAuthorId);
        Assert.assertThat(addedAuthor, is(notNullValue()));

        dbCommandTransactionalExecutor.execute(() -> {
            authorRepository.delete(addedAuthor);
            return null;
        });
        Assert.assertThat(authorRepository.findBy(addedAuthorId), is(nullValue()));
    }

    @Test
    public void existsById() {
        final Long addedAuthorId = dbCommandTransactionalExecutor.execute(() -> authorRepository.add(AuthorFactory.createMartinFowler()).getId());
        Assert.assertThat(authorRepository.existsBy(addedAuthorId), is(true));
    }

    @Test
    public void shouldGetAuthorSortedByName() {
        loadDataForFindByFilter();

        final PaginatedData<Author> result = authorRepository.findByFilter(new AuthorFilter());
        Assert.assertThat(result.getNumberOfRows(), is(4));
        Assert.assertThat(result.getRows().size(), is(4));
        Assert.assertThat(result.getRows().get(0).getName(), is(AuthorFactory.createErichGamma().getName()));
        Assert.assertThat(result.getRows().get(1).getName(), is(AuthorFactory.createMartinFowler().getName()));
        Assert.assertThat(result.getRows().get(2).getName(), is(AuthorFactory.createRobertMartin().getName()));
        Assert.assertThat(result.getRows().get(3).getName(), is(AuthorFactory.createUncleBob().getName()));
    }

    @Test
    public void shouldGetAuthorPaginated() {
        loadDataForFindByFilter();

        final AuthorFilter authorFilter = new AuthorFilter();
        authorFilter.setName("o");
        authorFilter.setPaginationData(new PaginationData(0, 2, "name", PaginationData.OrderMode.DESCENDING));

        PaginatedData<Author> result = authorRepository.findByFilter(authorFilter);
        Assert.assertThat(result.getNumberOfRows(), is(3));
        Assert.assertThat(result.getRows().size(), is(2));
        Assert.assertThat(result.getRows().get(0).getName(), is(AuthorFactory.createUncleBob().getName()));
        Assert.assertThat(result.getRows().get(1).getName(), is(AuthorFactory.createRobertMartin().getName()));

        authorFilter.setPaginationData(new PaginationData(2, 2, "name", PaginationData.OrderMode.DESCENDING));

        result = authorRepository.findByFilter(authorFilter);
        Assert.assertThat(result.getNumberOfRows(), is(3));
        Assert.assertThat(result.getRows().size(), is(1));
        Assert.assertThat(result.getRows().get(0).getName(), is(AuthorFactory.createMartinFowler().getName()));
    }

    private void loadDataForFindByFilter() {
        dbCommandTransactionalExecutor.execute(() -> {
            authorRepository.add(AuthorFactory.createMartinFowler());
            authorRepository.add(AuthorFactory.createRobertMartin());
            authorRepository.add(AuthorFactory.createUncleBob());
            authorRepository.add(AuthorFactory.createErichGamma());

            return null;
        });
    }
}
