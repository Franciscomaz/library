package com.library.app.common.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.author.model.Author;
import com.library.app.common.model.PaginatedData;

import java.util.List;

public final class JsonUtils {
    private JsonUtils() {
    }

    public static JsonElement getJsonElementWithId(final Long id) {
        final JsonObject jsonId = new JsonObject();
        jsonId.addProperty("id", id);
        return jsonId;
    }

    public static<T> JsonElement getJsonElementWithPagingEntries(final PaginatedData<T> paginatedData,
                                                           final EntityJsonConverter<T> entityJsonConverter) {
        JsonObject jsonWithEntriesAndPaging = new JsonObject();

        JsonObject jsonPaging = new JsonObject();
        jsonPaging.addProperty("totalRecords", paginatedData.getNumberOfRows());

        jsonWithEntriesAndPaging.add("paging", jsonPaging);
        jsonWithEntriesAndPaging.add("entries", entityJsonConverter.convertToJsonElement(paginatedData.getRows()));

        return jsonWithEntriesAndPaging;
    }
}
