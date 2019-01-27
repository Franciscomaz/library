package com.library.app.author.Service;

import com.library.app.author.model.Author;
import com.library.app.author.model.AuthorFilter;
import com.library.app.author.repository.AuthorRepository;
import com.library.app.common.exception.AuthorNotFound;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.model.PaginatedData;
import com.library.app.common.utils.ValidationUtils;

import javax.inject.Inject;
import javax.validation.Validator;
import java.util.List;

public class AuthorService implements IAuthorService {

    @Inject
    public Validator validator;

    @Inject
    public AuthorRepository authorRepository;

    @Override
    public Author add(Author author) throws FieldNotValidException {
        validateFields(author);

        return authorRepository.add(author);
    }

    @Override
    public Author update(Author author) throws FieldNotValidException, AuthorNotFound  {
        validateFields(author);

        if (!authorRepository.existsById(author.getId())){
            throw new AuthorNotFound();
        }

        return authorRepository.update(author);
    }

    @Override
    public Author findBy(Long id) throws AuthorNotFound {
        if (!authorRepository.existsById(id)){
            throw new AuthorNotFound();
        }

        return authorRepository.findBy(id);
    }

    @Override
    public PaginatedData<Author> findByFilter(AuthorFilter filter) {
        return authorRepository.findByFilter(filter);
    }

    @Override
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    private void validateFields(final Author author) {
        ValidationUtils.validateFields(author, validator);
    }
}
