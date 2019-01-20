package com.library.app.category.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.category.model.Category;
import com.library.app.category.service.ICategoryService;
import com.library.app.common.exception.CategoryNotFound;
import com.library.app.common.exception.DuplicatedCategoryException;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.json.JsonUtils;
import com.library.app.common.json.JsonWriter;
import com.library.app.common.json.OperationResultJsonWriter;
import com.library.app.common.model.HttpCode;
import com.library.app.common.model.OperationResult;
import com.library.app.common.model.ResourceMessage;
import com.library.app.common.model.StandardsOperationResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final ResourceMessage RESOURCE_MESSAGE = new ResourceMessage("category");

    @Inject
    ICategoryService categoryService;

    @Inject
    CategoryJsonConverter categoryJsonConverter;

    @POST
    public Response add(String body) {
        logger.debug("Adding a new category {}", body);

        Category category = categoryJsonConverter.convertFrom(body);

        HttpCode httpCode = HttpCode.CREATED;
        OperationResult result;

        try {
            category = categoryService.add(category);
            result = OperationResult.success(JsonUtils.getJsonElementWithId(category.getId()));
        } catch (FieldNotValidException e) {
            logger.error("One of the fiels of the category is not valid", e);
            httpCode = HttpCode.VALIDATION_ERROR;
            result = StandardsOperationResults.getOperationResultInvalidField(RESOURCE_MESSAGE, e);
        } catch (DuplicatedCategoryException e) {
            logger.error("There's already a category for the given name", e);
            httpCode = HttpCode.VALIDATION_ERROR;
            result = StandardsOperationResults.getOperationResultDuplicated(RESOURCE_MESSAGE, "name");
        }


        logger.debug("Returning the operation result after adding the category: {}", result);

        return Response
                .status(httpCode.getCode())
                .entity(OperationResultJsonWriter.toJson(result))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") long id, String body) {
        logger.debug("Updating a category {}", id, body);

        Category category = categoryJsonConverter.convertFrom(body);
        category.setId(id);

        HttpCode httpCode = HttpCode.OK;
        OperationResult result;

        try {
            category = categoryService.update(category);
            result = OperationResult.success(JsonUtils.getJsonElementWithId(category.getId()));
        } catch (FieldNotValidException e) {
            logger.error("One of the fiels of the category is not valid", e);
            httpCode = HttpCode.VALIDATION_ERROR;
            result = StandardsOperationResults.getOperationResultInvalidField(RESOURCE_MESSAGE, e);
        } catch (DuplicatedCategoryException e) {
            logger.error("There's already a category for the given name", e);
            httpCode = HttpCode.VALIDATION_ERROR;
            result = StandardsOperationResults.getOperationResultDuplicated(RESOURCE_MESSAGE, "name");
        } catch (CategoryNotFound e) {
            logger.error("No category found for the given id", e);
            httpCode = HttpCode.NOT_FOUND;
            result = StandardsOperationResults.getOperationResultResultNotFound(RESOURCE_MESSAGE);
        }


        logger.debug("Returning the operation result after updating the category: {}", result);

        return Response
                .status(httpCode.getCode())
                .entity(OperationResultJsonWriter.toJson(result))
                .build();
    }

    @GET
    @Path("/{id}")
    public Response findBy(@PathParam("id") long id) {
        logger.debug("Find category: {}", id);

        try {
            final Category foundCategory = categoryService.findBy(id);
            logger.debug("Category found: {}", foundCategory);
            return Response
                    .status(HttpCode.OK.getCode())
                    .entity(OperationResultJsonWriter.toJson(OperationResult.success(categoryJsonConverter.convertToJsonElement(foundCategory))))
                    .build();
        } catch (CategoryNotFound e) {
            logger.error("No category found for the given id", e);
            return Response
                    .status(HttpCode.NOT_FOUND.getCode())
                    .entity(OperationResultJsonWriter.toJson(StandardsOperationResults.getOperationResultResultNotFound(RESOURCE_MESSAGE)))
                    .build();
        }
    }

    @GET
    public Response findAll() {
        logger.debug("Find all categories");

        final List<Category> categories = categoryService.findAll();

        logger.debug("Found {} categories", categories);

        final JsonElement jsonWithPagingElements = getJsonElementWithPagingEntries(categories);

        return Response
                .status(HttpCode.OK.getCode())
                .entity(JsonWriter.writeToString(jsonWithPagingElements))
                .build();
    }

    private JsonElement getJsonElementWithPagingEntries(List<Category> categories) {
        JsonObject jsonWithEntriesAndPaging = new JsonObject();

        JsonObject jsonPaging = new JsonObject();
        jsonPaging.addProperty("totalRecords", categories.size());

        jsonWithEntriesAndPaging.add("paging", jsonPaging);
        jsonWithEntriesAndPaging.add("entries", categoryJsonConverter.convertToJsonElement(categories));

        return jsonWithEntriesAndPaging;
    }
}
