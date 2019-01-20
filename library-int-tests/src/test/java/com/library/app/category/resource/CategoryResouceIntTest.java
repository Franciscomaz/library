package com.library.app.category.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.library.app.category.model.Category;
import com.library.app.common.json.JsonReader;
import com.library.app.common.model.HttpCode;
import com.library.app.common.utils.category.CategoryFactory;
import com.library.app.commontests.utils.IntTestUtils;
import com.library.app.commontests.utils.ResourceClient;
import com.library.app.commontests.utils.ResourceDefinitions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URL;

import static com.library.app.common.utils.category.CategoryFactory.createAction;
import static com.library.app.common.utils.category.CategoryFactory.createFiction;
import static com.library.app.commontests.utils.FileTestNameUtils.getFileRequestPath;
import static com.library.app.commontests.utils.FileTestNameUtils.getFileResponsePath;
import static com.library.app.commontests.utils.JsonTestUtils.assertJsonMatchesFileContent;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

@RunWith(Arquillian.class)
public class CategoryResouceIntTest {

    @ArquillianResource
    private URL url;

    private ResourceClient resourceClient;

    private static final String PATH_RESOURCE = ResourceDefinitions.CATEGORY.getResourceName();

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap
                .create(WebArchive.class)
                .addPackages(true, "com.library.app")
                .addAsResource("persistence-integration.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setWebXML(new File("src/test/resources/web.xml"))
                .addAsLibraries(Maven
                        .resolver()
                        .resolve("com.google.code.gson:gson:2.3.1", "org.mockito:mockito-core:1.9.5")
                        .withTransitivity()
                        .asFile()
                );
    }

    @Before
    public void initTestCase() {
        this.resourceClient = new ResourceClient(url);

        resourceClient.resourcePath("/DB").delete();
    }

    @Test
    @RunAsClient
    public void shouldAddValidCategory() {
        final Long id = addCategoryAndGetId("category.json");
        findCategoryAndAssertReponseWithCategory(id, CategoryFactory.createFiction());
    }

    @Test
    @RunAsClient
    public void shouldNotAddCategoryWithNullName() {
        final Response response = resourceClient
                .resourcePath(PATH_RESOURCE).postWithFile(getFileRequestPath(PATH_RESOURCE, "categoryWithNullName.json"));

        Assert.assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "categoryErrorNullName.json");
    }

    @Test
    @RunAsClient
    public void shouldNotAddCategoryWithDuplicatedName() {
        resourceClient.resourcePath(PATH_RESOURCE).postWithFile(getFileRequestPath(PATH_RESOURCE, "category.json"));

        final Response response = resourceClient.resourcePath(PATH_RESOURCE)
                .postWithFile(getFileRequestPath(PATH_RESOURCE, "category.json"));

        Assert.assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "categoryAlreadyExists.json");
    }

    @Test
    @RunAsClient
    public void shouldUpdateCategory() {
        final Long id = addCategoryAndGetId("category.json");
        findCategoryAndAssertReponseWithCategory(id, CategoryFactory.createFiction());

        final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + id).putWithFile(
                getFileRequestPath(PATH_RESOURCE, "categoryAction.json")
        );
        Assert.assertThat(response.getStatus(), is(HttpCode.OK.getCode()));
        findCategoryAndAssertReponseWithCategory(id, createAction());
    }

    @Test
    @RunAsClient
    public void shouldNotUpdateCategoryWithNameBelongingToOtherCategory() {
        addCategoryAndGetId("category.json");

        final Long id = addCategoryAndGetId("categoryAction.json");
        findCategoryAndAssertReponseWithCategory(id, CategoryFactory.createAction());

        final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + id)
                .putWithFile(getFileRequestPath(PATH_RESOURCE, "category.json"));

        Assert.assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
    }

    @Test
    @RunAsClient
    public void shouldNotUpdateCategoryNotFound() {
        final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/999")
                .putWithFile(getFileRequestPath(PATH_RESOURCE, "category.json"));

        Assert.assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
    }

    @Test
    @RunAsClient
    public void shouldNotFindCategory() {
        final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/999").get();
        Assert.assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
    }

    @Test
    @RunAsClient
    public void shouldFindAllCategories() {
        resourceClient.resourcePath("DB/" + PATH_RESOURCE).postWithContent("");

        final Response response = resourceClient.resourcePath(PATH_RESOURCE).get();
        Assert.assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));

        assertResponseContainsTheCategories(response, 2, createAction(), createFiction());
    }

    private void assertResponseContainsTheCategories(final Response response, int expectedTotalRecords, Category... expectedCategories) {
        final JsonObject result = JsonReader.readAsJsonObject(response.readEntity(String.class));

        int totalRecords = result.getAsJsonObject("paging").get("totalRecords").getAsInt();
        Assert.assertThat(totalRecords, is(expectedTotalRecords));

        final JsonArray categoriesList = result.getAsJsonArray("entries");
        Assert.assertThat(categoriesList.size(), is(expectedCategories.length));

        for (int i = 0; i < expectedCategories.length; i++) {
            final Category expectedCategory = expectedCategories[i];
            Assert.assertThat(categoriesList.get(i).getAsJsonObject().get("name").getAsString(), is(expectedCategory.getName()));
        }
    }

    private Long addCategoryAndGetId(final String fileName) {
        return IntTestUtils.addElementWithFileAndGetId(resourceClient, PATH_RESOURCE, PATH_RESOURCE, fileName);
    }

    private void assertJsonResponseWithFile(final Response response, final String fileName) {
        assertJsonMatchesFileContent(response.readEntity(String.class), getFileResponsePath(PATH_RESOURCE, fileName));
    }

    private void findCategoryAndAssertReponseWithCategory(final Long categoryIdToBeFound, final Category expectedCategory) {
        String json = IntTestUtils.findById(resourceClient, PATH_RESOURCE, categoryIdToBeFound);

        JsonObject categoryAsJson = JsonReader.readAsJsonObject(json);
        Assert.assertThat(JsonReader.getStringOrNull(categoryAsJson, "name"), is(expectedCategory.getName()));
    }
}
