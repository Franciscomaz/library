package com.library.app.category.repository;

import com.library.app.category.model.Category;
import com.library.app.common.utils.category.CategoryFactory;
import com.library.app.common.utils.db.DBCommandTransactionalExecutor;
import com.library.app.common.utils.db.TestBaseRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class CategoryRepositoryTest extends TestBaseRepository {
    private CategoryRepository categoryRepository;

    @Before
    public void setUp() {
        initTestCase();

        categoryRepository = new CategoryRepository();
        categoryRepository.entityManager = entityManager;
    }

    @After
    public void setDown() {
        closeEntityManager();
    }

    @Test
    public void shouldAddCategory() {
        Category category = dbCommandTransactionalExecutor.execute(() -> categoryRepository.add(CategoryFactory.create()));

        assertThat(category, is(notNullValue()));
        assertThat(category.getName(), is(equalTo(category.getName())));
    }

    @Test
    public void shouldFindCategory() {
        Category addedCategory = dbCommandTransactionalExecutor.execute(() -> categoryRepository.add(CategoryFactory.create()));
        Category foundCategory = categoryRepository.findBy(addedCategory.getId());

        assertThat(foundCategory.getName(), is(equalTo(addedCategory.getName())));
    }

    @Test
    public void shouldReturnNull() {
        assertNull(categoryRepository.findBy(1L));
    }

    @Test
    public void shouldUpdateCategory() {
        Category addedCategory = dbCommandTransactionalExecutor.execute(() -> categoryRepository.add(CategoryFactory.createAction()));
        Category updatedCategory = dbCommandTransactionalExecutor.execute(() -> categoryRepository.update(CategoryFactory.createFiction()));
        Category foundCategory = categoryRepository.findBy(updatedCategory.getId());

        assertNotEquals(addedCategory.getName(), updatedCategory.getName());
        assertThat(foundCategory.getName(), is(equalTo(updatedCategory.getName())));
    }

    @Test
    public void shouldRemoveCategory() {
        Category addedCategory = dbCommandTransactionalExecutor.execute(() -> categoryRepository.add(CategoryFactory.createAction()));
        Category removedCategory = dbCommandTransactionalExecutor.execute(() -> categoryRepository.remove(categoryRepository.findBy(addedCategory.getId())));
        Category foundCategory = categoryRepository.findBy(removedCategory.getId());

        assertThat(foundCategory, is(nullValue()));
        assertThat(addedCategory.getName(), is(equalTo(removedCategory.getName())));
    }

    @Test
    public void shouldReturnTrueWhenCategoryAlreadyExists() {
        dbCommandTransactionalExecutor.execute(() -> categoryRepository.add(CategoryFactory.createAction()));

        assertThat(categoryRepository.alreadyExists(CategoryFactory.createAction()), is(true));
        assertThat(categoryRepository.alreadyExists(CategoryFactory.createFiction()), is(false));
    }

    @Test
    public void shouldReturnTrueWhenCategoryWithIdAlreadyExists() {
        final Category fiction = dbCommandTransactionalExecutor.execute(() -> {
            categoryRepository.add(CategoryFactory.createAction());
            return categoryRepository.add(CategoryFactory.createFiction());
        });

        assertThat(categoryRepository.alreadyExists(fiction), is(false));

        fiction.setName(CategoryFactory.createAction().getName());
        assertThat(categoryRepository.alreadyExists(fiction), is(true));

        fiction.setName("teste");
        assertThat(categoryRepository.alreadyExists(fiction), is(false));
    }

    @Test
    public void shouldReturnTrueWhenCategoryExistsById() {
        final Category addedCategory = dbCommandTransactionalExecutor.execute(() -> categoryRepository.add(CategoryFactory.createFiction()));
        assertThat(categoryRepository.existsById(addedCategory.getId()), is(true));

        addedCategory.setId(9999L);
        assertThat(categoryRepository.existsById(addedCategory.getId()), is(false));
    }
}
