package com.library.app.category.service;

import com.library.app.category.model.Category;
import com.library.app.category.repository.CategoryRepository;
import com.library.app.common.exception.CategoryNotFound;
import com.library.app.common.exception.DuplicatedCategoryException;
import com.library.app.common.exception.FieldNotValidException;
import org.junit.Before;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;

import java.util.ArrayList;
import java.util.List;

import static com.library.app.common.utils.category.CategoryFactory.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CategoryServiceTest {
    private Validator validator;
    private ICategoryService categoryService;
    private CategoryRepository categoryRepository;

    @Before
    public void initTestCase() {
        categoryRepository = mock(CategoryRepository.class);
        validator = Validation.buildDefaultValidatorFactory().getValidator();

        categoryService = new CategoryService();
        ((CategoryService) categoryService).validator = validator;
        ((CategoryService) categoryService).categoryRepository = categoryRepository;
    }

    @Test
    public void shouldNotAddCategoryWithNullName() {
        addCategoryWithInvalidName(null);
    }

    @Test
    public void shouldNotAddCategoryWithShortName() {
        addCategoryWithInvalidName("A");
    }

    @Test
    public void shouldNotAddCategoryWithLongName() {
        addCategoryWithInvalidName("AAAAAAAAAAAAAAAAAAAAAAAAAA");
    }

    @Test(expected = DuplicatedCategoryException.class)
    public void shouldNotAddDuplicatedCategory() {
        when(categoryRepository.alreadyExists(any(Category.class))).thenReturn(true);
        categoryService.add(createFiction());
    }

    @Test
    public void shouldAddValidCategory() {
        when(categoryRepository.alreadyExists(any(Category.class))).thenReturn(false);
        when(categoryRepository.add(any(Category.class))).thenReturn(createFiction());

        Category addedCategory = categoryService.add(createFiction());
        assertThat(createFiction().getName(), is(equalTo(addedCategory.getName())));
    }

    @Test
    public void shouldUpdateValidCategory() {
        final Long id = 1L;
        when(categoryRepository.alreadyExists(createAction())).thenReturn(false);
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.update(any(Category.class))).thenReturn(createFiction());

        Category updatedCategory = categoryService.update(createWithId(createFiction(), id));
        assertThat(updatedCategory.getName(), is(equalTo(createFiction().getName())));
    }

    @Test(expected = CategoryNotFound.class)
    public void shouldNotFindCategoryById() {
        final long id = 1L;
        when(categoryRepository.existsById(id)).thenReturn(false);

        categoryService.findBy(id);
    }

    @Test
    public void shouldNotFindCategories() {
        when(categoryRepository.findAll()).thenReturn(new ArrayList<>());

        List<Category> categories = categoryService.findAll();
        assertThat(categories.isEmpty(), is(true));
    }

    @Test
    public void shouldFindAllCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(createAction());
        categories.add(createFiction());
        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> foundCategories = categoryService.findAll();
        assertThat(foundCategories.isEmpty(), is(false));
        assertThat(foundCategories.size(), is(2));
        assertThat(foundCategories.get(0).getName(), is(equalTo(createAction().getName())));
        assertThat(foundCategories.get(1).getName(), is(equalTo(createFiction().getName())));
    }

    @Test
    public void shouldFindCategoryById() {
        final long id = 1L;
        when(categoryRepository.existsById(id)).thenReturn(true);
        when(categoryRepository.findBy(id)).thenReturn(createWithId(createFiction(), id));

        Category foundCategory = categoryRepository.findBy(id);
        assertThat(foundCategory, is(notNullValue()));
        assertThat(foundCategory.getId(), is(equalTo(id)));
        assertThat(foundCategory.getName(), is(equalTo(createFiction().getName())));
    }

    @Test(expected = CategoryNotFound.class)
    public void shouldNotUpdateWhenCategoryIsNotFound() {
        final Long id = 1L;
        when(categoryRepository.alreadyExists(createWithId(createAction(), id))).thenReturn(false);
        when(categoryRepository.existsById(id)).thenReturn(false);

        categoryService.update(createAction());
    }

    private void addCategoryWithInvalidName(final String name) {
        try {
            categoryService.add(create(name));
            fail("An error should have been throw");
        } catch (FieldNotValidException e) {
            assertThat(e.field(), is(equalTo("name")));
        }
    }
}
