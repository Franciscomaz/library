package com.library.app.category.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.category.model.Category;
import com.library.app.common.json.JsonReader;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class CategoryJsonConverter {
    public Category convertFrom(String json) {
        JsonObject jsonObject = JsonReader.readAsJsonObject(json);

        return new Category(JsonReader.getStringOrNull(jsonObject, "name"));
    }

    public JsonElement convertToJsonElement(List<Category> categories) {
        JsonArray jsonArray = new JsonArray();
        for (Category category : categories) {
            jsonArray.add(convertToJsonElement(category));
        }
        return jsonArray;
    }

    public JsonElement convertToJsonElement(Category category) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", category.getId());
        jsonObject.addProperty("name", category.getName());
        return jsonObject;
    }
}
