package io.kennethmartens.ckm.service;

import io.kennethmartens.ckm.data.Blog;
import io.kennethmartens.ckm.streaming.producers.BlogProducer;
import io.kennethmartens.ckm.streaming.repository.BlogInteractiveQueryService;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.NotFoundException;
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
                    blog.setDateModified(new Date());
                    blog.setLoves(0);

                    return blog;
                })
                .flatMap(this::updateBlogEvent);
    }

    public Uni<Response> update(String id, Blog blog) {
        return this.findById(id)
                .map(foundBlog -> {
                    foundBlog.setTitle(blog.getTitle());
                    foundBlog.setSubtitle(blog.getSubtitle());
                    foundBlog.setIntroduction(blog.getIntroduction());
                    foundBlog.setBody(blog.getBody());
                    foundBlog.setHeaderImageReference(blog.getHeaderImageReference());

                    foundBlog.setDateModified(new Date());
                    return foundBlog;
                })
                .call(updatedBlog -> this.blogProducer.produceEvent(id, updatedBlog))
                // Transform result.
                .map(galleryUpdatedEvent -> Response.noContent().build());
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


    public Uni<Response> getById(String id) {
        return this.findById(id)
                .map(blog -> Response.ok(blog).build());
    }

    private Uni<Blog> findById(String id) {
        return Uni.createFrom()
                .item(blogInteractiveQueryService.getById(id))
                .onItem().ifNull().failWith(new NotFoundException(
                        String.format("Gallery with id %1$s not found", id)
                ));
    }

    public Uni<Response> deleteById(String id) {
        return Uni.createFrom()
                .item(id)
                .call(x -> this.findById(id))
                // Send tombstone
                .call(item -> this.blogProducer.produceEvent(id,null))
                .map(galleryUni -> Response.noContent().build());
    }

}
