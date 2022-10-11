package io.kennethmartens.ckm.rest.v1.resources;

import io.kennethmartens.ckm.data.Blog;
import io.kennethmartens.ckm.service.BlogServiceImpl;
import io.kennethmartens.ckm.streaming.TopologyProducer;
import io.kennethmartens.ckm.streaming.entities.ServerSentEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import io.smallrye.reactive.messaging.kafka.Record;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.resteasy.reactive.ResponseHeader;
import org.jboss.resteasy.reactive.RestSseElementType;
import org.jboss.resteasy.reactive.RestStreamElementType;
import org.jboss.resteasy.reactive.common.NotImplementedYet;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.print.Book;
import java.util.Set;
import java.util.function.Function;

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
    @PUT
    public Uni<Response> updateBlog(String id, Blog blog) {
        log.info("PUT request to {} with id {} and body {}", API_BLOG, id, blog);
        return this.blogService.update(id, blog);
    }

    @Path("{id}")
    @RolesAllowed({"blog-admin"})
    @DELETE
    public Uni<Response> deleteBlogById(String id) {
        log.info("DELETE request to {} with id {}", API_BLOG, id);
        return this.blogService.deleteById(id);
    }

    /**
     * Server Sent Events.
     */
    @Channel("ckm-blogs-incoming")
    Multi<Record<String, Blog>> blogs;
    @Path("/stream")
    @ResponseHeader(name = "X-Accel-Buffering", value = "no")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    @GET
    public Multi<ServerSentEvent<String,Blog>> getBlogStream() {
        return blogs.onItem().transform(stringBlogRecord -> new ServerSentEvent<>(stringBlogRecord.key(), stringBlogRecord.value()));

    }
}
