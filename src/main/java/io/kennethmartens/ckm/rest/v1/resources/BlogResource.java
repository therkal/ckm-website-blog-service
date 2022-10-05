package io.kennethmartens.ckm.rest.v1.resources;

import io.kennethmartens.ckm.data.Blog;
import io.kennethmartens.ckm.service.BlogServiceImpl;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Slf4j
@Path(BlogResource.API_BLOG)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BlogResource {

    private final BlogServiceImpl blogService;
    public static final String API_BLOG = "/blogs";

    public BlogResource(BlogServiceImpl blogService) {
        this.blogService = blogService;
    }

    @GET
    public Uni<Response> getBlogPosts() {
        return this.blogService.get();
    }

    @Path("{id}")
    @GET
    public Uni<Response> getById(String id) {
        return this.blogService.getById(id);
    }

    @RolesAllowed({"blog-admin"})
    @POST
    public Uni<Response> createBlog(Blog blog) {
        log.info("POST request to {} with {}", API_BLOG, blog);
        return this.blogService.persist(blog);
    }

    @Path("{id}")
    @RolesAllowed({"blog-admin"})
    @DELETE
    public Uni<Response> deleteBlogById(String id) {
        log.info("DELETE request to {} with id {}", API_BLOG, id);
        return this.blogService.deleteById(id);
    }
}
