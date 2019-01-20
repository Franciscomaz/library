package com.library.app.commontests.utils;

import org.junit.Ignore;

@Ignore
public enum ResourceDefinitions {
    CATEGORY("categories");

    private String resourceName;

    ResourceDefinitions(final String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceName() {
        return resourceName;
    }
}
