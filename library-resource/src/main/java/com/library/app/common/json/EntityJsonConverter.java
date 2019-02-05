package com.library.app.common.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.List;

public interface EntityJsonConverter<T> {

    T convertFrom(String json);

    JsonElement convertToJsonElement(T entity);

    default JsonElement convertToJsonElement(List<T> entities) {
        JsonArray jsonArray = new JsonArray();
        for (T entity : entities) {
            jsonArray.add(convertToJsonElement(entity));
        }
        return jsonArray;
    }
}
