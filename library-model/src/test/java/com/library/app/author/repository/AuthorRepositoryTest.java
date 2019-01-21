package com.library.app.author.repository;

import com.library.app.author.model.Author;
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
}
