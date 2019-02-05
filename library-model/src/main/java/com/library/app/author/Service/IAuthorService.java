package com.library.app.author.Service;

import com.library.app.author.model.Author;
import com.library.app.author.model.AuthorFilter;
import com.library.app.common.exception.AuthorNotFound;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.model.PaginatedData;

import java.util.List;

public interface IAuthorService {
    Author add(Author author) throws FieldNotValidException;
    Author update(Author author) throws FieldNotValidException, AuthorNotFound;
    Author findBy(Long id) throws AuthorNotFound;
    PaginatedData<Author> findBy(AuthorFilter filter);
    List<Author> findAll();
}
