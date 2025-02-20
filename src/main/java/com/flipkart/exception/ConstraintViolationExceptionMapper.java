package com.flipkart.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.hibernate.exception.ConstraintViolationException;
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException>
{
    @Override
    public Response toResponse(ConstraintViolationException exception)
    {
        String message = "A unique constraint was violated";
        if (exception.getSQLException().getMessage().contains("username"))
        {
            message = "Username already exists";
        }
        else if (exception.getSQLException().getMessage().contains("title"))
        {
            message = "Title already exists";
        }
        return Response.status(Response.Status.CONFLICT).entity(message).build();
    }
}
