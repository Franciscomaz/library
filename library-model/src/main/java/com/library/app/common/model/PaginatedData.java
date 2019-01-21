package com.library.app.common.model;

import java.util.List;

public class PaginatedData<T> {
    private final int numberOfRows;
    private final List<T> rows;

    public PaginatedData(int numberOfRows, List<T> rows) {
        this.numberOfRows = numberOfRows;
        this.rows = rows;
    }

    public List<T> getRows() {
        return rows;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public T getRow(int index) {
        if(index >= rows.size()) {
            return null;
        }
        return rows.get(index);
    }
}
