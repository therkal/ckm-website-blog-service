package io.kennethmartens.ckm.streaming;

import io.kennethmartens.ckm.data.Blog;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import io.quarkus.kafka.client.serialization.ObjectMapperSerde;
import io.smallrye.mutiny.subscription.MultiEmitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.HashSet;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class TopologyProducer {

    public final static String CKM_BLOGS_STORE = "ckm-blogs-store";

    public final static String CKM_BLOGS_TOPIC = "ckm-blogs";

    ObjectMapperSerde<Blog> blogObjectMapperSerde = new ObjectMapperSerde<>(
            Blog.class
    );

    @Produces
    public Topology buildTopology() {

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KeyValueBytesStoreSupplier storeSupplier = Stores.persistentKeyValueStore(
                CKM_BLOGS_STORE);

        GlobalKTable<String, Blog> s = streamsBuilder.globalTable(
                CKM_BLOGS_TOPIC,
                Consumed.with(Serdes.String(), blogObjectMapperSerde),
                Materialized.<String, Blog> as(storeSupplier)
                        .withKeySerde(Serdes.String())
                        .withValueSerde(blogObjectMapperSerde)
        );

        return streamsBuilder.build();
    }
}
