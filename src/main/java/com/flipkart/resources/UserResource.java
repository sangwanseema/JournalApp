package com.flipkart.resources;

import com.flipkart.core.User;
import com.flipkart.db.UserDao;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path("/Users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class UserResource {
    private final UserDao userDao;
    public UserResource(UserDao userDao) {
        this.userDao = userDao;
    }

    @GET// GET method to get all users
     @UnitOfWork
    public List<User> getAllUsers() {
        return userDao.getAllUsers(); // Calling DAO to get the users from the database
    }

@POST // Post METHOD to create new user
@UnitOfWork
public Response createUser(User user) {
    try {
        User createdUser = userDao.saveUser(user);
        return Response.status(Response.Status.CREATED).entity(createdUser).build();
    } catch (WebApplicationException e) {
        return Response.status(e.getResponse().getStatus()).entity(e.getMessage()).build();
    }
}

    @GET // GET method to get user by username
    @Path("/{username}") // Define the path parameter
    @UnitOfWork
    public Response getUserByUsername(@PathParam("username") String username) {
        return userDao.findByUsername(username)
                .map(user -> Response.ok(user).build()) // 200 OK if user is found
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found with username: " + username)
                        .build()); // 404 Not Found if user is not found
    }


//    @PUT
//    @Path("/{username}") // Define the path for updating user details
//    @UnitOfWork
//    public Response updateUser(@PathParam("username") String username, User updatedUser) {
//        Optional<User> existingUserOptional = userDao.findByUsername(username);
//
//        if (existingUserOptional.isEmpty()) {
//            return Response.status(Response.Status.NOT_FOUND)
//                    .entity("User not found with username: " + username)
//                    .build();
//        }
//
//        User existingUser = existingUserOptional.get();
//
//        // Update only provided fields, keeping existing values for null fields
//        if (updatedUser.getUsername() != null) {
//            existingUser.setUsername(updatedUser.getUsername());
//        }
//        if (updatedUser.getEmail() != null) {
//            existingUser.setEmail(updatedUser.getEmail());
//        }
//        // Add other fields similarly
//
//        try {
//            User savedUser = userDao.saveUser(existingUser);
//            return Response.ok(savedUser).build();
//        } catch (Exception e) {
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                    .entity("Error updating user")
//                    .build();
//        }
//    }
//
//
@PUT
@Path("/{username}") // Define the path for updating user details
@UnitOfWork
public Response updateUser(@PathParam("username") String username, User updatedUser) {
    Optional<User> existingUserOptional = userDao.findByUsername(username);

    if (existingUserOptional.isEmpty()) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity("User not found with username: " + username)
                .build();
    }

    User existingUser = existingUserOptional.get();

    // Check if user is trying to change username to an already existing one
    if (updatedUser.getUsername() != null && !updatedUser.getUsername().equals(username)) {
        Optional<User> userWithSameUsername = userDao.findByUsername(updatedUser.getUsername());
        if (userWithSameUsername.isPresent()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Username already exists")
                    .build();
        }
        existingUser.setUsername(updatedUser.getUsername());
    }

    // Update only provided fields
    if (updatedUser.getEmail() != null) {
        existingUser.setEmail(updatedUser.getEmail());
    }
    // Add other fields as needed

    try {
        User savedUser = userDao.saveUser(existingUser); // Using the existing saveUser method
        return Response.ok(savedUser).build();
    } catch (WebApplicationException e) {
        return Response.status(e.getResponse().getStatus())
                .entity(e.getMessage())
                .build();
    } catch (Exception e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error updating user")
                .build();
    }
}
    @DELETE
    @Path("/{username}") // Define the path for deleting a user by username
    @UnitOfWork
    public Response deleteUser(@PathParam("username") String username) {
        Optional<User> existingUserOptional = userDao.findByUsername(username);

        if (existingUserOptional.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("User not found with username: " + username)
                    .build();
        }

        try {
            userDao.deleteUser(existingUserOptional.get());
            return Response.status(Response.Status.NO_CONTENT).build(); // 204 No Content
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting user")
                    .build();
        }
    }


//    public static class ErrorResponse {
//        public String error;
//
//        public ErrorResponse(String error) {
//            this.error = error;
//        }
//    }



//    @GET
//    public String check()
//    {
//        return "OK";
//    }
    //    // POST method to create a new user

//    @POST
//     @UnitOfWork
//    public Response createUser(User user) {
//        User createdUser = userDao.saveUser(user); // Creating a user in the database
//        return Response.status(Response.Status.CREATED).entity(createdUser).build();
//
//
//    }
}
