package io.kennethmartens.ckm.streaming.repository;

import io.kennethmartens.ckm.data.Blog;
import io.kennethmartens.ckm.streaming.TopologyProducer;
import io.smallrye.mutiny.Multi;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.jboss.resteasy.reactive.common.NotImplementedYet;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@ApplicationScoped
public class BlogInteractiveQueryService {

    @Inject
    KafkaStreams streams;

    public Set<Blog> get() {
        Set<Blog> blogs = new HashSet<>();

        getStore().all()
                // Add to list
                .forEachRemaining(stringGalleryKeyValue -> {
                    // Filter out tombstoned records.
                    if(stringGalleryKeyValue.value != null)
                        blogs.add(stringGalleryKeyValue.value);
                });

        return blogs;
    }

    public Blog getById(String id) {
        return getStore()
                .get(id);
    }

    private ReadOnlyKeyValueStore<String, Blog> getStore() {
        while (true) {
            try {
                return streams.store(StoreQueryParameters.fromNameAndType(TopologyProducer.CKM_BLOGS_STORE, QueryableStoreTypes.keyValueStore()));
            } catch (InvalidStateStoreException e) {
                // ignore, store not ready yet
            }
        }
    }


}