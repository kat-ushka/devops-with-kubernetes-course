package com.github.katushka.devopswithkubernetescourse.resource;

import com.github.katushka.devopswithkubernetescourse.service.PingPongService;
import com.github.katushka.devopswithkubernetescourse.service.TimestampService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;

@Path("/")
public class GeneratedStringResource {

    private final String randomString = UUID.randomUUID().toString();

    private final TimestampService timestampService;

    private final PingPongService pingPongService;

    public GeneratedStringResource() {
        timestampService = new TimestampService();
        pingPongService = new PingPongService();
    }

    public String getCode() {
        return MessageFormat.format(
                "{0}\n{1}\t{2}\nPing / Pongs: {3}",
                Optional.ofNullable(System.getenv("MESSAGE")).orElse("No message"),
                timestampService.getTimestamp(), randomString, pingPongService.getPingPongsCount());
    }

    @GET
    public Response getCodeResponse() {
        return Response.ok().entity(getCode()).build();
    }
}
