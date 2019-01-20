package com.library.app.category.resource;

import com.library.app.category.model.Category;
import com.library.app.category.service.CategoryService;
import com.library.app.common.exception.CategoryNotFound;
import com.library.app.common.exception.DuplicatedCategoryException;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.model.HttpCode;
import com.library.app.commontests.utils.ResourceDefinitions;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;

import java.util.Arrays;
import java.util.Collections;

import static com.library.app.common.utils.category.CategoryFactory.createAction;
import static com.library.app.common.utils.category.CategoryFactory.createFiction;
import static com.library.app.common.utils.category.CategoryFactory.createWithId;
import static com.library.app.commontests.utils.FileTestNameUtils.getFileRequestPath;
import static com.library.app.commontests.utils.FileTestNameUtils.getFileResponsePath;
import static com.library.app.commontests.utils.JsonTestUtils.assertJsonMatchesFileContent;
import static com.library.app.commontests.utils.JsonTestUtils.assertMatchesExpectedJson;
import static com.library.app.commontests.utils.JsonTestUtils.readJsonFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class CategoryResourceTest {
    private static final String PATH_RESOURCE = ResourceDefinitions.CATEGORY.getResourceName();
    private CategoryResource categoryResource;

    @Mock
    private CategoryService categoryService;

    @Before
    public void initTestCase() {
        MockitoAnnotations.initMocks(this);
        categoryResource = new CategoryResource();

        categoryResource.categoryService = categoryService;
        categoryResource.categoryJsonConverter = new CategoryJsonConverter();
    }

    @Test
    public void shouldAddValidCategory() {
        when(categoryService.add(any(Category.class))).thenReturn(createWithId(createFiction(), 1L));

        final Response response = categoryResource.add(readJsonFile(getFileRequestPath(PATH_RESOURCE, "newCategory.json")));
        assertThat(response.getStatus(), is(HttpCode.CREATED.getCode()));
        assertMatchesExpectedJson(response.getEntity().toString(), "{\"id\": 1}");
    }

    @Test
    public void shouldAddDuplicatedCategory() {
        when(categoryService.add(any(Category.class))).thenThrow(new DuplicatedCategoryException());

        final Response response = categoryResource.add(readJsonFile(getFileRequestPath(PATH_RESOURCE, "newCategory.json")));
        assertThat(response.getStatus(), is(HttpCode.VALIDATION_ERROR.getCode()));
        assertJsonResponseWithFile(response, "categoryAlreadyExists.json");
    }

    @Test
    public void shouldAddCategoryWithNullName() {
        when(categoryService.add(any(Category.class))).thenThrow(new FieldNotValidException("name", "may not be null"));

        final Response response = categoryResource.add(readJsonFile(getFileRequestPath(PATH_RESOURCE, "newCategory.json")));
        assertThat(response.getStatus(), is(HttpCode.VALIDATION_ERROR.getCode()));
        assertJsonResponseWithFile(response, "categoryErrorNullName.json");
    }

    @Test
    public void shouldUpdateCategory() {
        when(categoryService.update(createWithId(createFiction(), 1L))).thenReturn(createWithId(createAction(), 1L));

        Response response = categoryResource.update(1L, readJsonFile(getFileRequestPath(PATH_RESOURCE, "newCategory.json")));
        assertThat(response.getStatus(), is(HttpCode.OK.getCode()));
        assertThat(response.getEntity().toString(), is("{\"id\":1}"));
    }

    @Test
    public void shouldNotUpdateCategoryWithNameBelongingToOtherCategory() {
        when(categoryService.update(createWithId(createFiction(), 1L))).thenThrow(new DuplicatedCategoryException());

        Response response = categoryResource.update(1L, readJsonFile(getFileRequestPath(PATH_RESOURCE, "category.json")));
        assertThat(response.getStatus(), is(HttpCode.VALIDATION_ERROR.getCode()));
        assertJsonResponseWithFile(response, "categoryAlreadyExists.json");
    }

    @Test
    public void shouldNotUpdateCategoryWithNullName() {
        doThrow(new FieldNotValidException("name", "may not be null")).when(categoryService).update(createWithId(createFiction(), 1L));

        Response response = categoryResource.update(1L, readJsonFile(getFileRequestPath(PATH_RESOURCE, "category.json")));
        assertThat(response.getStatus(), is(HttpCode.VALIDATION_ERROR.getCode()));
        assertJsonResponseWithFile(response, "categoryErrorNullName.json");
    }

    @Test
    public void shouldNotUpdateCategoryNotFound() {
        doThrow(new CategoryNotFound()).when(categoryService).update(createWithId(createFiction(), 1L));

        Response response = categoryResource.update(1L, readJsonFile(getFileRequestPath(PATH_RESOURCE, "category.json")));
        assertThat(response.getStatus(), is(HttpCode.NOT_FOUND.getCode()));
        assertJsonResponseWithFile(response, "categoryNotFound.json");
    }

    @Test
    public void shouldFindACategory() {
        when(categoryService.findBy(1L)).thenReturn(createWithId(createFiction(), 1L));

        Response response = categoryResource.findBy(1L);
        assertThat(response.getStatus(), is(HttpCode.OK.getCode()));
        assertJsonResponseWithFile(response, "categoryFound.json");
    }

    @Test
    public void shouldNotFindACategory() {
        doThrow(new CategoryNotFound()).when(categoryService).findBy(1L);

        Response response = categoryResource.findBy(1L);
        assertThat(response.getStatus(), is(HttpCode.NOT_FOUND.getCode()));
        assertJsonResponseWithFile(response, "categoryNotFound.json");
    }

    @Test
    @Ignore
    public void shouldFindAllCategories() {
        when(categoryService.findAll()).thenReturn(Arrays.asList(createWithId(createFiction(), 1L), createWithId(createAction(), 2L)));

        Response response = categoryResource.findAll();
        assertThat(response.getStatus(), is(HttpCode.OK.getCode()));
        assertJsonResponseWithFile(response, "twoCategories.json");
    }

    @Test
    @Ignore
    public void shouldNotFindAnyCategory() {
        when(categoryService.findAll()).thenReturn(Collections.emptyList());

        Response response = categoryResource.findAll();
        assertThat(response.getStatus(), is(HttpCode.OK.getCode()));
        assertJsonResponseWithFile(response, "emptyListOfCategories.json");
    }

    private void assertJsonResponseWithFile(Response response, String filename) {
        assertJsonMatchesFileContent(response.getEntity().toString(), getFileResponsePath(PATH_RESOURCE, filename));
    }
}
