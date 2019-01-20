package com.library.app.commontests.utils;

import com.library.app.common.model.HttpCode;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Ignore;

import javax.ws.rs.core.Response;

import static com.library.app.commontests.utils.FileTestNameUtils.getFileRequestPath;

@Ignore
public class IntTestUtils {

    public static Long addElementWithFileAndGetId(final ResourceClient resourceClient,
                                                  final String pathResource,
                                                  final String mainFolder,
                                                  final String fileName) {
        final Response response = resourceClient.resourcePath(pathResource).postWithFile(
                getFileRequestPath(mainFolder, fileName)
        );

        return assertResponseIsCreatedAndGetId(response);
    }

    public static String findById(ResourceClient resourceClient, String pathResource, Long id) {
        final Response response = resourceClient.resourcePath(pathResource + "/" + id).get();
        Assert.assertThat(response.getStatus(), CoreMatchers.is(HttpCode.OK.getCode()));
        return response.readEntity(String.class);
    }

    private static Long assertResponseIsCreatedAndGetId(Response response) {
        Assert.assertThat(response.getStatus(), CoreMatchers.is(HttpCode.CREATED.getCode()));
        final Long id = JsonTestUtils.getIdFromJson(response.readEntity(String.class));
        Assert.assertThat(id, CoreMatchers.is(CoreMatchers.notNullValue()));
        return id;
    }
}
