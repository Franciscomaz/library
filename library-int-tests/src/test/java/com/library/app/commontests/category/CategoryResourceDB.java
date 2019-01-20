package com.library.app.commontests.category;

import com.library.app.category.service.ICategoryService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static com.library.app.common.utils.category.CategoryFactory.allCategories;

@Stateless
@Path("/DB/categories")
@Produces({MediaType.APPLICATION_JSON})
public class CategoryResourceDB {

    @Inject
    private ICategoryService categoryService;

    @POST
    public void addAll() {
        allCategories().forEach(categoryService::add);
    }

}
