package com.pedantic.rest;


import com.pedantic.config.SecureAuth;
import com.pedantic.config.SecurityFilter;
import com.pedantic.entity.TodoUser;
import com.pedantic.service.MySession;
import com.pedantic.service.PersistenceService;
import com.pedantic.service.QueryService;
import com.pedantic.service.SecurityUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.inject.Inject;
import javax.json.Json;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.*;
import java.security.Key;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

@Path("user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TodoUserRest {


    //GET
    //POST
    //PUT
    //DELETE

    @Inject
    private PersistenceService persistenceService;

    @Inject
    private QueryService queryService;
    @Inject
    private SecurityUtil securityUtil;

    @Context
    private UriInfo uriInfo;

    @Inject
    private Logger logger;

    @Inject
    private MySession mySession;


    @Path("create") //api/v1/user/create -TodoUser(Json) - POST
    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
    public Response createTodoUser(@NotNull @Valid TodoUser todoUser) {


        persistenceService.saveTodoUser(todoUser);

        return Response.ok(todoUser).build();

    }


    @PUT
    @Path("update")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
    @SecureAuth
    public Response updateTodoUser(@NotNull @Valid TodoUser user) {
        persistenceService.updateTodoUser(user);

        return Response.ok(user).build();
    }


    @GET
    @Path("find/{email}") // /api/v1/user/find/{email}/users/{age}
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
    @SecureAuth
    public TodoUser findTodoUserByEmail(@NotNull @PathParam("email") String email) {

        return queryService.findTodoUserByEmail(email);
    }


    @GET
    @Path("query") // /api/v1/user/query?email=bla@bla.com
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
    @SecureAuth
    public TodoUser findTodoUserByEmailQueryParam(@NotNull @QueryParam("email") String email) {

        return queryService.findTodoUserByEmail(email);
    }


    @GET
    @Path("search")
    @SecureAuth
    public Response searchTodoUserByName(@NotNull @QueryParam("name") String name) {

        return Response.ok(queryService.findTodoUsersByName(name)).build();
    }

    @GET
    @Path("count")
    @SecureAuth
    public Response countTodoUserByEmail(@QueryParam("email") @NotNull String email) {

        return Response.ok(queryService.countTodoUserByEmail(email)).build();
    }

    @GET
    @Path("list")
    @SecureAuth
    public Response listAllTodoUsers() {

        return Response.ok(queryService.findAllTodoUsers()).build();
    }

    @PUT
    @Path("update-email")
    @SecureAuth
    public Response updateEmail(@NotNull @QueryParam("id") Long id, @NotNull @QueryParam("email") String email) {
        TodoUser todoUser = persistenceService.updateTodoUserEmail(id, email);

        return Response.ok(todoUser).build();


    }


    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@NotEmpty(message = "Email field must be set") @FormParam("email") String email,
                          @NotEmpty(message = "Password field must be set") @FormParam("password") String password) {


        //Authenticate user
        //Generate token
        //Return token in Response header to client



        if (!securityUtil.authenticateUser(email, password)) {
            throw new SecurityException("Email or password is invalid");
        }

        String token = getToken(email);
        mySession.setEmail(email);


        return Response.ok().header(AUTHORIZATION, SecurityFilter.BEARER + " " + token).build();
    }


    private String getToken(String email) {
        Key key = securityUtil.generateKey();


        String token = Jwts.builder().setSubject(email).setIssuer(uriInfo.getAbsolutePath().toString())

                .setIssuedAt(new Date()).setExpiration(securityUtil.toDate(LocalDateTime.now().plusMinutes(15)))

                .signWith(SignatureAlgorithm.HS512, key).setAudience(uriInfo.getBaseUri().toString())

                .compact();

        logger.log(Level.INFO, "Generated token is {0}", token);


        return token;
    }

}
