package com.library.app.common.model.filter;

import java.util.Objects;

public class GenericFilter {
    private PaginationData paginationData;

    public GenericFilter() {
    }

    public GenericFilter(PaginationData paginationData) {
        this.paginationData = paginationData;
    }

    public PaginationData getPaginationData() {
        return paginationData;
    }

    public void setPaginationData(PaginationData paginationData) {
        this.paginationData = paginationData;
    }

    public boolean hasPaginationData() {
        return Objects.nonNull(paginationData);
    }

    public boolean hasOrderField() {
        return hasPaginationData() && paginationData.hasOrderField();
    }

    @Override
    public String toString() {
        return "GenericFilter{" +
                "paginationData=" + paginationData +
                '}';
    }
}
