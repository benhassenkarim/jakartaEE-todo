package com.pedantic.config;

import com.pedantic.service.MySession;
import com.pedantic.service.SecurityUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import sun.security.krb5.internal.AuthorizationData;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Key;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;


@Provider //Register filter with JAX-RS runtime
@SecureAuth //Bind SecureAuth annotation with this filter
@Priority(Priorities.AUTHENTICATION) //Prioritise this filter above others as a security filter
public class SecurityFilter implements ContainerRequestFilter {
    public static final String BEARER = "Bearer";

    @Inject
    private Logger logger;

    @Inject
    private SecurityUtil securityUtil;

    @Inject
    private MySession mySession;


    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        //Grab token from the header of the request using the AUTHORIZATION constant
        //Throw an exception with a message if there's no token
        //Parse the token
        //If parsing succeeds, proceed
        //Otherwise we throw an exception with message


        String authString = requestContext.getHeaderString(AUTHORIZATION);

        if (authString == null || authString.isEmpty() || !authString.startsWith(BEARER)) {

            logger.log(Level.WARNING, "No valid string token found");

            JsonObject jsonObject = Json.createObjectBuilder().add("error-message", "No valid string token found").build();

            throw new NotAuthorizedException(Response.status(Response.Status.UNAUTHORIZED).entity(jsonObject).build());
        }

        String token = authString.substring(BEARER.length()).trim();

        //We need to instantiate a new SecurityContext object
        //We need to pass the new SC object to the RC



        try {

            Key key = securityUtil.generateKey();
//            Jwts.parser().setSigningKey(key).parse(token);

            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(key).parseClaimsJws(token);


            SecurityContext originalSecurityContext = requestContext.getSecurityContext();

            SecurityContext newSecurityContext = new SecurityContext() {

                @Override
                public Principal getUserPrincipal() {
                    return new Principal() {
                        @Override
                        public String getName() {
                            return claimsJws.getBody().getSubject();

                        }
                    };
                }

                @Override
                public boolean isUserInRole(String role) {
                    return originalSecurityContext.isUserInRole(role);
                }

                @Override
                public boolean isSecure() {
                    return originalSecurityContext.isSecure();
                }

                @Override
                public String getAuthenticationScheme() {
                    return originalSecurityContext.getAuthenticationScheme();
                }
            };

            requestContext.setSecurityContext(newSecurityContext);



        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());

        }


    }
}
