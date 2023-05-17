package com.pedantic.config;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {



    @Override
    public Response toResponse(ConstraintViolationException exception) {

        JsonObjectBuilder errorBuilder = Json.createObjectBuilder().add("Error", "There were errors in the data sent.");
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

        for (ConstraintViolation constraintViolation : exception.getConstraintViolations()) {
            String property = constraintViolation.getPropertyPath().toString().split("\\.")[2];
            String message = constraintViolation.getMessage();

            objectBuilder.add(property, message);

//            myViolationMap.put(property, message);
        }

        errorBuilder.add("violatedFields", objectBuilder.build());

        return Response.status(Response.Status.EXPECTATION_FAILED).entity(errorBuilder.build()).build();
    }
}
