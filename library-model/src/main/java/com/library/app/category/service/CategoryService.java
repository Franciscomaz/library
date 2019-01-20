package com.library.app.category.service;

import com.library.app.category.model.Category;
import com.library.app.category.repository.CategoryRepository;
import com.library.app.common.exception.CategoryNotFound;
import com.library.app.common.exception.DuplicatedCategoryException;
import com.library.app.common.exception.FieldNotValidException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Stateless
public class CategoryService implements ICategoryService {

    @Inject
    Validator validator;

    @Inject
    CategoryRepository categoryRepository;

    @Override
    public Category add(Category category) {
        validateCategory(category);

        return categoryRepository.add(category);
    }

    @Override
    public Category update(Category category) throws FieldNotValidException, DuplicatedCategoryException {
        validateCategory(category);

        if (!categoryRepository.existsById(category.getId())) {
            throw new CategoryNotFound();
        }

        return categoryRepository.update(category);
    }

    @Override
    public Category findBy(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFound();
        }
        return categoryRepository.findBy(id);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    private void validateCategory(Category category) {
        validateFields(category);

        if (categoryRepository.alreadyExists(category)) {
            throw new DuplicatedCategoryException();
        }
    }

    private void validateFields(Category category) {
        final Set<ConstraintViolation<Category>> errors = validator.validate(category);
        final Iterator<ConstraintViolation<Category>> itErrors = errors.iterator();

        if (itErrors.hasNext()) {
            ConstraintViolation<Category> violation = itErrors.next();
            throw new FieldNotValidException(violation.getPropertyPath().toString(), violation.getMessage());
        }
    }
}
