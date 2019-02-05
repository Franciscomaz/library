package com.library.app.commontests.utils;

import org.junit.Ignore;

@Ignore
public enum ResourceDefinitions {
    AUTHOR("authors"),
    CATEGORY("categories");

    private String resourceName;

    ResourceDefinitions(final String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceName() {
        return resourceName;
    }
}
