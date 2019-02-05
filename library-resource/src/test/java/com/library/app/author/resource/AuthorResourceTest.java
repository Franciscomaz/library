package com.library.app.author.resource;

import com.library.app.author.Service.AuthorService;
import com.library.app.author.model.Author;
import com.library.app.author.model.AuthorFilter;
import com.library.app.common.exception.AuthorNotFound;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.model.HttpCode;
import com.library.app.common.model.PaginatedData;
import com.library.app.commontests.utils.ResourceDefinitions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.util.Arrays;
import java.util.List;

import static com.library.app.common.utils.author.AuthorFactory.*;
import static com.library.app.commontests.utils.FileTestNameUtils.getFileRequestPath;
import static com.library.app.commontests.utils.FileTestNameUtils.getFileResponsePath;
import static com.library.app.commontests.utils.JsonTestUtils.assertJsonMatchesFileContent;
import static com.library.app.commontests.utils.JsonTestUtils.assertMatchesExpectedJson;
import static com.library.app.commontests.utils.JsonTestUtils.readJsonFile;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthorResourceTest {
    private static final String PATH_RESOURCE = ResourceDefinitions.AUTHOR.getResourceName();

    @InjectMocks
    private AuthorResource authorResource;

    @Mock
    private AuthorService authorService;

    @Mock
    private UriInfo uriInfo;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        authorResource.authorJsonConverter = new AuthorJsonConverter();
    }

    @Test
    public void shouldAddAuthor() {
        when(authorService.add(any(Author.class))).thenReturn(createWithId(1L, createMartinFowler()));

        final Response response = authorResource.add(readJsonFile(getFileRequestPath(PATH_RESOURCE, "martinFowler.json")));

        Assert.assertThat(response.getStatus(), is(HttpCode.CREATED.getCode()));
        assertMatchesExpectedJson(response.getEntity().toString(), "{\"id\": 1}");
    }

    @Test
    public void shouldNotAddAuthorWithNullName() {
        when(authorService.add(any(Author.class))).thenThrow(new FieldNotValidException("name", "may not be null"));

        final Response response = authorResource.add(readJsonFile(getFileRequestPath(PATH_RESOURCE, "authorWithNullName.json")));

        Assert.assertThat(response.getStatus(), is(HttpCode.VALIDATION_ERROR.getCode()));
        assertJsonResponseWithFile(response, "authorErrorNullName.json");
    }

    @Test
    public void shouldUpdateAuthor() {
        when(authorService.update(any(Author.class))).thenReturn(createWithId(1L, createMartinFowler()));

        final Response response = authorResource.update(1L, readJsonFile(getFileRequestPath(PATH_RESOURCE, "martinFowler.json")));
        Assert.assertThat(response.getStatus(), is(HttpCode.OK.getCode()));
        assertMatchesExpectedJson(response.getEntity().toString(), "{\"id\": 1}");

        verify(authorService).update(createWithId(1L, createMartinFowler()));
    }

    @Test
    public void shouldNotUpdateAuthorWithNull() {
        when(authorService.update(any(Author.class))).thenThrow(new FieldNotValidException("name", "may not be null"));

        final Response response = authorResource.update(2L, readJsonFile(getFileRequestPath(PATH_RESOURCE, "authorWithNullName.json")));
        Assert.assertThat(response.getStatus(), is(HttpCode.VALIDATION_ERROR.getCode()));
        assertJsonResponseWithFile(response, "authorErrorNullName.json");
    }

    @Test
    public void shouldNotUpdateAuthorNotFound() {
        when(authorService.update(any(Author.class))).thenThrow(new AuthorNotFound());

        final Response response = authorResource.update(2L, readJsonFile(getFileRequestPath(PATH_RESOURCE, "martinFowler.json")));
        Assert.assertThat(response.getStatus(), is(HttpCode.NOT_FOUND.getCode()));
    }

    @Test
    public void updateAuthorNotFound() {
        when(authorService.update(any(Author.class))).thenThrow(new AuthorNotFound());

        final Response response = authorResource.update(2L, readJsonFile(getFileRequestPath(PATH_RESOURCE, "martinFowler.json")));
        Assert.assertThat(response.getStatus(), is(HttpCode.NOT_FOUND.getCode()));
    }

    @Test
    public void shouldFindAuthorById() {
        when(authorService.findBy(1L)).thenReturn(createWithId(1L, createMartinFowler()));

        final Response response = authorResource.findBy(1L);
        Assert.assertThat(response.getStatus(), is(HttpCode.OK.getCode()));
    }

    @Test
    public void shouldNotFindAuthor() {
        when(authorService.findBy(1L)).thenThrow(new AuthorNotFound());

        final Response response = authorResource.findBy(1L);
        Assert.assertThat(response.getStatus(), is(HttpCode.NOT_FOUND.getCode()));
    }

    @Test
    public void shouldFindByFilter() {
        final List<Author> authors = Arrays.asList(
                createWithId(1L, createMartinFowler()),
                createWithId(2L, createRobertMartin()),
                createWithId(3L, createErichGamma()),
                createWithId(4L, createUncleBob()));

        final MultivaluedMap<String, String> multiMap = mock(MultivaluedMap.class);
        when(uriInfo.getQueryParameters()).thenReturn(multiMap);

        when(authorService.findBy(any(AuthorFilter.class)))
                .thenReturn(new PaginatedData<>(authors.size(), authors));

        final Response response = authorResource.findByFilter();
        Assert.assertThat(response.getStatus(), is(HttpCode.OK.getCode()));
        assertJsonResponseWithFile(response, "authorsAllInOnePage.json");
    }

    private void assertJsonResponseWithFile(Response response, String filename) {
        assertJsonMatchesFileContent(response.getEntity().toString(), getFileResponsePath(PATH_RESOURCE, filename));
    }
}
