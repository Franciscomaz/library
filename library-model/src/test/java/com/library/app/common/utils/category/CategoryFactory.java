package com.library.app.common.utils.category;

import com.library.app.category.model.Category;
import org.junit.Ignore;

import java.util.Arrays;
import java.util.List;

@Ignore
public class CategoryFactory {
    public static String defaultName = "Teste";

    public static Category create() {
        return new Category(defaultName);
    }

    public static Category create(String name) {
        return new Category(name);
    }

    public static Category createAction() {
        return CategoryFactory.create("Action");
    }

    public static Category createFiction() {
        return new Category("Fiction");
    }

    public static Category createWithId(Category category, Long id) {
        category.setId(id);
        return category;
    }

    public static List<Category> allCategories() {
        return Arrays.asList(createAction(), createFiction());
    }
}
