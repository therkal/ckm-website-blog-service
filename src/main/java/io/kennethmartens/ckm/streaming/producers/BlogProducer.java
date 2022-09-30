package io.kennethmartens.ckm.streaming.producers;


import io.kennethmartens.ckm.data.Blog;
import io.kennethmartens.ckm.streaming.TopologyProducer;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.Record;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.enterprise.context.ApplicationScoped;

@Slf4j
@ApplicationScoped
public class BlogProducer {

    @Channel(TopologyProducer.CKM_BLOGS_TOPIC)
    Emitter<Record<String, Blog>> emitter;

    public Uni<Blog> produceEvent(String id, Blog blog) {
        log.debug("Producing Blog updated event with key {} and data {}", id, blog != null ? blog : "Tombstone");

        this.emitter.send(
            Record.of(
                id,
                blog
            )
        );

        return Uni.createFrom().item(blog);
    }

}
