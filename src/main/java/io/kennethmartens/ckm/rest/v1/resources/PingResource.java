package io.kennethmartens.ckm.rest.v1.resources;

import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Slf4j
@Path(PingResource.API_PING)
public class PingResource {
    public static final String API_PING = "/ping";

    @GET
    public Uni<String> get() {
        log.info("GET request to {}", API_PING);
        return Uni.createFrom().item("Application up and running");
    }
}
