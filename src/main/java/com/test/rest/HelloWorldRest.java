package com.test.rest;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;

@Path("hello")

public class HelloWorldRest {


    @Path("{name}")
    @GET
    public JsonObject greet(@PathParam("name") String name) {

        return Json.createObjectBuilder().add("name", name)
                .add("greeting", "Hello, " + name)
                .add("message", "Hello from Jakarta EE!").build();
    }



}
