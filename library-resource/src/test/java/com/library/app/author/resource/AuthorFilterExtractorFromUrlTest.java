package com.library.app.author.resource;

import com.library.app.author.model.AuthorFilter;
import com.library.app.common.model.filter.PaginationData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class AuthorFilterExtractorFromUrlTest {

    @Mock
    private UriInfo uriInfo;

    @Before
    public void initTestCase() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetNullValues(){
        setUpUriInfo(null, null, null, null);

        AuthorFilterExtractorFromUrl extractorFromUrl = new AuthorFilterExtractorFromUrl(uriInfo);
        AuthorFilter authorFilter = extractorFromUrl.getFilter();

        assertActualPaginationDataWithExpected(authorFilter.getPaginationData(), new PaginationData(0, 10, "name", PaginationData.OrderMode.ASCENDING));
        Assert.assertThat(authorFilter.getName(), is(nullValue()));
    }

    @Test
    public void shouldGetPaginationWithNameAndSortByIdAscending(){
        setUpUriInfo("2", "5", "Martin", "id");

        AuthorFilterExtractorFromUrl extractorFromUrl = new AuthorFilterExtractorFromUrl(uriInfo);
        AuthorFilter authorFilter = extractorFromUrl.getFilter();

        assertActualPaginationDataWithExpected(authorFilter.getPaginationData(), new PaginationData(10, 5, "id", PaginationData.OrderMode.ASCENDING));
        assertThat(authorFilter.getName(), is("Martin"));
    }

    @SuppressWarnings("unchecked")
    private void setUpUriInfo(String page, String perPage, String name, String sort) {
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("page", page);
        parameters.put("per_page", perPage);
        parameters.put("name", name);
        parameters.put("sort", sort);

        MultivaluedMap<String, String> multiMap = Mockito.mock(MultivaluedMap.class);

        for (Map.Entry<String, String> keyValue : parameters.entrySet()) {
            when(multiMap.getFirst(keyValue.getKey())).thenReturn(keyValue.getValue());
        }

        when(uriInfo.getQueryParameters()).thenReturn(multiMap);
    }

    private void assertActualPaginationDataWithExpected(PaginationData actual, PaginationData expected) {
        assertThat(actual.getFirstResult(), is(expected.getFirstResult()));
        assertThat(actual.getMaxResults(), is(expected.getMaxResults()));
        assertThat(actual.getOrderField(), is(expected.getOrderField()));
        assertThat(actual.getOrderMode(), is(expected.getOrderMode()));
    }
}
