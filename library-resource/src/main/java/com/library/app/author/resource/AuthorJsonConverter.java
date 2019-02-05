package com.library.app.author.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.author.model.Author;
import com.library.app.common.json.EntityJsonConverter;
import com.library.app.common.json.JsonReader;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthorJsonConverter implements EntityJsonConverter<Author> {
    public Author convertFrom(String json) {
        JsonObject jsonObject = JsonReader.readAsJsonObject(json);

        return new Author(JsonReader.getStringOrNull(jsonObject, "name"));
    }

    public JsonElement convertToJsonElement(Author author) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", author.getId());
        jsonObject.addProperty("name", author.getName());
        return jsonObject;
    }
}
