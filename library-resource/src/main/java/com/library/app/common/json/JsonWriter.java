package com.library.app.common.json;

import com.google.gson.Gson;

import java.util.Objects;

public class JsonWriter {

    private JsonWriter() {
    }

    public static String writeToString(final Object object) {
        if (Objects.isNull(object)) {
            return "";
        }
        return new Gson().toJson(object);
    }
}
