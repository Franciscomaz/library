package com.library.app.common.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.common.exception.InvalidJson;

public class JsonReader {

    public static JsonObject readAsJsonObject(final String json) throws InvalidJson  {
        return readJsonAs(json, JsonObject.class);
    }

    public static JsonArray readAsJsonArray(final String json) throws InvalidJson {
        return readJsonAs(json, JsonArray.class);
    }

    public static <T> T readJsonAs(String json, Class<T> jsonClass) throws InvalidJson {
        if(json == null || json.trim().isEmpty()) {
            throw new InvalidJson("Json string cannot be null");
        }
        try {
            return new Gson().fromJson(json, jsonClass);
        } catch (Exception e) {
            throw new InvalidJson("Json string cannot be null");
        }
    }

    public static Long getLongOrNull(final JsonObject jsonObject, final String propertyName) {
        final JsonElement property = jsonObject.get(propertyName);
        if(isJsonElementNull(property)) {
            return null;
        }
        return property.getAsLong();
    }

    public static Integer getIntegerOrNull(final JsonObject jsonObject, final String propertyName) {
        final JsonElement property = jsonObject.get(propertyName);
        if(isJsonElementNull(property)) {
            return null;
        }
        return property.getAsInt();
    }

    public static String getStringOrNull(final JsonObject jsonObject, final String propertyName) {
        final JsonElement property = jsonObject.get(propertyName);
        if(isJsonElementNull(property)) {
            return null;
        }
        return property.getAsString();
    }

    public static Double getDoubleOrNull(final JsonObject jsonObject, final String propertyName) {
        final JsonElement property = jsonObject.get(propertyName);
        if(isJsonElementNull(property)) {
            return null;
        }
        return property.getAsDouble();
    }

    private static boolean isJsonElementNull(JsonElement element) {
        return element == null || element.isJsonNull();
    }
}
