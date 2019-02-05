package com.library.app.author.resource;

import com.library.app.author.Service.AuthorService;
import com.library.app.author.model.Author;
import com.library.app.author.model.AuthorFilter;
import com.library.app.common.exception.AuthorNotFound;
import com.library.app.common.exception.DuplicatedCategoryException;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.json.JsonUtils;
import com.library.app.common.json.JsonWriter;
import com.library.app.common.json.OperationResultJsonWriter;
import com.library.app.common.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static com.library.app.common.json.JsonUtils.getJsonElementWithPagingEntries;

@Path("/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthorResource {

    private static final ResourceMessage RESOURCE_MESSAGE = new ResourceMessage("author");

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private AuthorService authorService;

    AuthorJsonConverter authorJsonConverter;

    @Inject
    @Context
    private UriInfo uriInfo;

    @POST
    public Response add(String body) {
        logger.debug("Adding a new category {}", body);

        Author author = authorJsonConverter.convertFrom(body);

        HttpCode httpCode = HttpCode.CREATED;
        OperationResult result;

        try {
            author = authorService.add(author);
            result = OperationResult.success(JsonUtils.getJsonElementWithId(author.getId()));
        } catch (FieldNotValidException e) {
            logger.error("One of the fiels of the author is not valid", e);
            httpCode = HttpCode.VALIDATION_ERROR;
            result = StandardsOperationResults.getOperationResultInvalidField(RESOURCE_MESSAGE, e);
        } catch (DuplicatedCategoryException e) {
            logger.error("There's already a author for the given name", e);
            httpCode = HttpCode.VALIDATION_ERROR;
            result = StandardsOperationResults.getOperationResultDuplicated(RESOURCE_MESSAGE, "name");
        }


        logger.debug("Returning the operation result after adding the author: {}", result);

        return Response
                .status(httpCode.getCode())
                .entity(OperationResultJsonWriter.toJson(result))
                .build();
    }

    public Response update(long id, String body) {

        logger.debug("Updating a author {}", id, body);

        Author author = authorJsonConverter.convertFrom(body);
        author.setId(id);

        HttpCode httpCode = HttpCode.OK;
        OperationResult result;

        try {
            author = authorService.update(author);
            result = OperationResult.success(JsonUtils.getJsonElementWithId(author.getId()));
        } catch (FieldNotValidException e) {
            logger.error("One of the fiels of the author is not valid", e);
            httpCode = HttpCode.VALIDATION_ERROR;
            result = StandardsOperationResults.getOperationResultInvalidField(RESOURCE_MESSAGE, e);
        } catch (AuthorNotFound e) {
            logger.error("No author found for the given id", e);
            httpCode = HttpCode.NOT_FOUND;
            result = StandardsOperationResults.getOperationResultResultNotFound(RESOURCE_MESSAGE);
        }

        logger.debug("Returning the operation result after updating the author: {}", result);

        return Response
                .status(httpCode.getCode())
                .entity(OperationResultJsonWriter.toJson(result))
                .build();
    }

    public Response findBy(long id) {
        logger.debug("Find author: {}", id);

        try {
            final Author foundAuthor = authorService.findBy(id);
            logger.debug("Author found: {}", foundAuthor);
            return Response
                    .status(HttpCode.OK.getCode())
                    .entity(OperationResultJsonWriter.toJson(OperationResult.success(authorJsonConverter.convertToJsonElement(foundAuthor))))
                    .build();
        } catch (AuthorNotFound e) {
            logger.error("No author found for the given id", e);
            return Response
                    .status(HttpCode.NOT_FOUND.getCode())
                    .entity(OperationResultJsonWriter.toJson(StandardsOperationResults.getOperationResultResultNotFound(RESOURCE_MESSAGE)))
                    .build();
        }
    }

    public Response findByFilter() {
        final AuthorFilter authorFilter = new AuthorFilterExtractorFromUrl(uriInfo).getFilter();

        logger.debug("Finding authors using filter: {}", authorFilter);

        final PaginatedData<Author> paginatedAuthors = authorService.findBy(authorFilter);

        logger.debug("Found {} authors", paginatedAuthors.getNumberOfRows());

        return Response
                .status(HttpCode.OK.getCode())
                .entity(JsonWriter.writeToString(getJsonElementWithPagingEntries(paginatedAuthors, authorJsonConverter)))
                .build();
    }
}
