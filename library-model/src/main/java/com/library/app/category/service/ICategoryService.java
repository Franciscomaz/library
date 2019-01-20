package com.library.app.category.service;

import com.library.app.category.model.Category;
import com.library.app.common.exception.CategoryNotFound;
import com.library.app.common.exception.DuplicatedCategoryException;
import com.library.app.common.exception.FieldNotValidException;

import javax.ejb.Local;
import java.util.List;

@Local
public interface ICategoryService {
    Category add(Category category) throws FieldNotValidException, DuplicatedCategoryException;
    Category update(Category category) throws FieldNotValidException, DuplicatedCategoryException, CategoryNotFound;
    Category findBy(Long id);
    List<Category> findAll();
}
