package com.library.app.author.resource;

import com.library.app.author.model.AuthorFilter;
import com.library.app.common.model.filter.PaginationData;

import javax.ws.rs.core.UriInfo;
import java.util.Objects;

public class AuthorFilterExtractorFromUrl {
    private UriInfo uriInfo;

    public AuthorFilterExtractorFromUrl(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public AuthorFilter getFilter() {
        final AuthorFilter authorFilter = new AuthorFilter();
        authorFilter.setName(uriInfo.getQueryParameters().getFirst("name"));
        authorFilter.setPaginationData(extractPaginationData());
        return authorFilter;
    }

    private PaginationData extractPaginationData() {
        final int perPage = getPerPage();
        final int firstResult = getPage() * perPage;

        String orderField;
        PaginationData.OrderMode orderMode;

        final String sortField = getSortField();

        if (sortField.startsWith("+")) {
            orderField = sortField.substring(1);
            orderMode = PaginationData.OrderMode.ASCENDING;
        } else if (sortField.startsWith("-")) {
            orderField = sortField.substring(1);
            orderMode = PaginationData.OrderMode.DESCENDING;
        } else {
            orderField = sortField;
            orderMode = PaginationData.OrderMode.ASCENDING;
        }

        return new PaginationData(firstResult, perPage, orderField, orderMode);
    }

    protected String getSortField() {
        final String sortField = uriInfo.getQueryParameters().getFirst("sort");

        if (Objects.isNull(sortField)) {
            return "name";
        }

        return sortField;
    }

    private Integer getPage() {
        final String page = uriInfo.getQueryParameters().getFirst("page");

        if (Objects.isNull(page)) {
            return 0;
        }

        return Integer.parseInt(page);
    }

    private Integer getPerPage() {
        final String perPage = uriInfo.getQueryParameters().getFirst("per_page");

        if(Objects.isNull(perPage)) {
            return 10;
        }

        return Integer.parseInt(perPage);
    }

}
