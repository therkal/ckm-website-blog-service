package io.kennethmartens.ckm.service;

import io.kennethmartens.ckm.data.Blog;
import io.kennethmartens.ckm.streaming.producers.BlogProducer;
import io.kennethmartens.ckm.streaming.repository.BlogInteractiveQueryService;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@ApplicationScoped
public class BlogServiceImpl {

    private final BlogProducer blogProducer;
    private final BlogInteractiveQueryService blogInteractiveQueryService;

    public BlogServiceImpl(BlogProducer blogProducer, BlogInteractiveQueryService blogInteractiveQueryService) {
        this.blogProducer = blogProducer;
        this.blogInteractiveQueryService = blogInteractiveQueryService;
    }

    public Uni<Response> persist(Blog blog) {
        return Uni.createFrom()
                .item(blog)
                // Set ID
                .map(galleryToBeCreated -> {
                    // Generate UUID
                    String uuid = UUID.randomUUID().toString();

                    blog.setId(uuid);
                    blog.setDatePosted(new Date());
                    blog.setLoves(0);

                    return blog;
                })
                .flatMap(this::updateBlogEvent);
    }

    private Uni<Response> updateBlogEvent(Blog toBeCreated) {
        return Uni.createFrom()
                .item(toBeCreated)
                // Persist
                .call(gallery -> this.blogProducer.produceEvent(toBeCreated.getId(), toBeCreated))
                // Transform result.
                .map(galleryUpdatedEvent -> Response.noContent().build());
    }

    public Uni<Response> get() {
        return Uni.createFrom()
                .item(blogInteractiveQueryService.get())
                .map(galleries -> Response.ok(galleries).build());
    }
}
