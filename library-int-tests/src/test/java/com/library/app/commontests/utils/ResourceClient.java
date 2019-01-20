package com.library.app.commontests.utils;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

import static com.library.app.commontests.utils.JsonTestUtils.readJsonFile;

public class ResourceClient {
    private URL urlBase;
    private String resourcePath;

    public ResourceClient(URL urlBase) {
        this.urlBase = urlBase;
    }

    public ResourceClient resourcePath(final String resourcePath) {
        this.resourcePath = resourcePath;
        return this;
    }

    public Response postWithFile(String fileName) {
        return postWithContent(getRequestFromFileOrEmptyIfNullFile(fileName));
    }

    public Response postWithContent(String content) {
        return buildClient().post(Entity.entity(content, MediaType.APPLICATION_JSON));
    }

    public Response putWithFile(final String fileName) {
        return putWithContent(getRequestFromFileOrEmptyIfNullFile(fileName));
    }

    public Response putWithContent(final String content) {
        return buildClient().put(Entity.entity(content, MediaType.APPLICATION_JSON));
    }

    public Response get() {
        return buildClient().get();
    }

    private Invocation.Builder buildClient() {
        final Client resourceCLient = ClientBuilder.newClient();
        return resourceCLient.target(getFullUrl(resourcePath)).request();
    }

    private String getFullUrl(String resourcePath) {
        try{
            return this.urlBase.toURI() + "api/" + resourcePath;
        } catch (final URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private String getRequestFromFileOrEmptyIfNullFile(final String fileName) {
        return Objects.nonNull(fileName) ? readJsonFile(fileName) : "";
    }

    public void delete() {
        buildClient().delete();
    }
}
