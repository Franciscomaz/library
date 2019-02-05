package com.library.app.service;

import com.library.app.author.Service.AuthorService;
import com.library.app.author.Service.IAuthorService;
import com.library.app.author.model.Author;
import com.library.app.author.model.AuthorFilter;
import com.library.app.author.repository.AuthorRepository;
import com.library.app.common.exception.AuthorNotFound;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.model.PaginatedData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collections;

import static com.library.app.common.utils.author.AuthorFactory.createMartinFowler;
import static com.library.app.common.utils.author.AuthorFactory.createWithId;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

public class AuthorServiceTest {
    private static Validator validator;
    private IAuthorService authorService;

    @Mock
    private AuthorRepository authorRepository;

    @BeforeClass
    public static void setUpClass() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Before
    public void setUpTestCase() {
        MockitoAnnotations.initMocks(this);

        authorService = new AuthorService();

        ((AuthorService)authorService).authorRepository = authorRepository;
        ((AuthorService)authorService).validator = validator;
    }

    @Test
    public void shouldAddAuthor() {
        when(authorRepository.add(createMartinFowler())).thenReturn(createWithId(1L, createMartinFowler()));

        final Author addedAuthor = authorService.add(createMartinFowler());

        try {
            Assert.assertThat(addedAuthor, is(notNullValue()));
            Assert.assertThat(addedAuthor.getId(), is(1L));
            Assert.assertThat(addedAuthor.getName(), is(createMartinFowler().getName()));
        } catch (FieldNotValidException e){
            fail("No error should have been throw.");
        }
    }

    @Test
    public void shouldNotAddAuthorWithNullName() {
        shouldNotAddAuthorWithInvalidName(null);
    }

    @Test
    public void shouldNotAddAuthorWithShortName() {
        shouldNotAddAuthorWithInvalidName("A");
    }

    @Test
    public void shouldNotAddAuthorWithLongName() {
        shouldNotAddAuthorWithInvalidName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }

    @Test
    public void shouldUpdateAuthor() {
        when(authorRepository.existsById(1L)).thenReturn(true);
        when(authorRepository.update(createWithId(1L, createMartinFowler()))).thenReturn(createWithId(1L, createMartinFowler()));

        final Author author = authorService.update(createWithId(1L, createMartinFowler()));

        Assert.assertThat(author, is(notNullValue()));
        Assert.assertThat(author.getId(), is(1L));
        Assert.assertThat(author.getName(), is(createMartinFowler().getName()));
    }

    @Test(expected = AuthorNotFound.class)
    public void shouldNotUpdateNonExistentAuthor() {
        when(authorRepository.existsById(1L)).thenReturn(false);
        authorService.update(createWithId(1L, createMartinFowler()));
    }

    @Test
    public void shouldNotUpdateAuthorWithNullName() {
        shouldNotUpdateAuthorWithInvalidName(null);
    }

    @Test
    public void shouldNotUpdateAuthorWithShortName() {
        shouldNotUpdateAuthorWithInvalidName("A");
    }

    @Test
    public void shouldNotUpdateAuthorWithLongName() {
        shouldNotUpdateAuthorWithInvalidName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }

    @Test
    public void shouldNotFindAuthor() {
        when(authorRepository.existsById(1L)).thenReturn(false);
        try {
            authorService.findBy(1L);
            fail("Should have throw an error.");
        } catch (AuthorNotFound e) {}
    }

    @Test
    public void shouldFindAuthor() {
        when(authorRepository.existsById(1L)).thenReturn(true);
        when(authorRepository.findBy(1L)).thenReturn(createWithId(1L, createMartinFowler()));

        final Author foundAuthor = authorService.findBy(1L);

        Assert.assertThat(foundAuthor.getId(), is(1L));
        Assert.assertThat(foundAuthor.getName(), is(createMartinFowler().getName()));
    }

    @Test
    public void shouldFindAuthorPaginated() {
        final PaginatedData<Author> authors = new PaginatedData<>(1, Collections.singletonList(createWithId(1L, createMartinFowler())));
        when(authorRepository.findByFilter(anyObject())).thenReturn(authors);

        final PaginatedData<Author> authorsReturned = authorService.findBy(new AuthorFilter());
        Assert.assertThat(authorsReturned.getNumberOfRows(), is(1));
        Assert.assertThat(authorsReturned.getRow(0).getName(), is(createMartinFowler().getName()));
    }

    private void shouldNotAddAuthorWithInvalidName(final String name) {
        try {
            authorService.add(new Author(name));
            fail("An error should have been throw");
        } catch (FieldNotValidException e){
            Assert.assertThat(e.field(), is("name"));
        }
    }

    private void shouldNotUpdateAuthorWithInvalidName(final String name) {
        try {
            authorService.add(new Author(name));
            fail("An error should have been throw");
        } catch (FieldNotValidException e){
            Assert.assertThat(e.field(), is("name"));
        } catch (final AuthorNotFound e) {
            fail("An author not found exception should have not been throw");
        }
    }
}
