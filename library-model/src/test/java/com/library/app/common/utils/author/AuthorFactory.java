package com.library.app.common.utils.author;

import com.library.app.author.model.Author;

public class AuthorFactory {


    public static Author createWithId(final Long id, final Author author) {
        author.setId(id);
        return author;
    }

    public static Author createRobertMartin() {
        return new Author("Robert Martin");
    }

    public static Author createMartinFowler() {
        return new Author("Martin Fowler");
    }

    public static Author createUncleBob() {
        return new Author("Uncle Bob");
    }

    public static Author createErichGamma() {
        return new Author("Erich Gamma");
    }
}
