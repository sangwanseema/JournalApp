package com.flipkart.resources;

import com.flipkart.core.Journal;
import com.flipkart.core.User;
import com.flipkart.db.JournalDao;
import com.flipkart.db.UserDao;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path("/Journal")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class JournalResource {
    private final JournalDao journalDao;
    private final UserDao userDao;

    public JournalResource(JournalDao journalDao, UserDao userDao) {
        this.journalDao = journalDao;
        this.userDao = userDao;
    }


    @GET
    @UnitOfWork
    public List<Journal> getJournals() {
        return journalDao.getAllJournals();
    }

    @POST
    @UnitOfWork
    @Path("/{username}") // Pass username in the URL
    public Response createJournal(@PathParam("username") String username, Journal journal) {
        // Find user by username
        Optional<User> userOptional = userDao.findByUsername(username);

        if (userOptional.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("User not found with username: " + username)
                    .build();
        }

        User user = userOptional.get(); // Extract User from Optional
        journal.setUser(user); // Assign the user to the journal

        // Save the journal in the database
        Journal createdJournal = journalDao.saveJournal(journal);

        return Response.status(Response.Status.CREATED)
                .entity(createdJournal)
                .build();
    }

    @DELETE
    @UnitOfWork
    @Path("/{title}") // Delete journal by title
    public Response deleteJournalByTitle(@PathParam("title") String title) {
        // Find the journal by title
        Optional<Journal> journalOptional = journalDao.findByTitle(title);

        if (journalOptional.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Journal not found with title: " + title)
                    .build();
        }

        Journal journal = journalOptional.get();
        User user = journal.getUser(); // Get associated user

        if (user != null) {
            user.getJournals().remove(journal); // Remove journal from user's list
            userDao.saveUser(user); // Save user after removing reference
        }

        journalDao.deleteJournal(journal); // Delete journal from DB

        return Response.status(Response.Status.NO_CONTENT).build();
    }
    @PUT
    @UnitOfWork
    @Path("/{title}") // Define the path to update a journal by title
    public Response updateJournalByTitle(@PathParam("title") String title, Journal updatedJournal) {
        Optional<Journal> existingJournalOptional = journalDao.findByTitle(title);

        if (existingJournalOptional.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Journal not found with title: " + title)
                    .build();
        }

        Journal existingJournal = existingJournalOptional.get();

        // Check if the user is trying to change the title to an already existing one
        if (updatedJournal.getTitle() != null && !updatedJournal.getTitle().equals(title)) {
            Optional<Journal> journalWithSameTitle = journalDao.findByTitle(updatedJournal.getTitle());
            if (journalWithSameTitle.isPresent()) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("A journal with this title already exists")
                        .build();
            }
            existingJournal.setTitle(updatedJournal.getTitle());
        }

        // Update only provided fields
        if (updatedJournal.getContent() != null) {
            existingJournal.setContent(updatedJournal.getContent());
        }

        try {
            Journal savedJournal = journalDao.saveJournal(existingJournal); // Save updated journal
            return Response.ok(savedJournal).build();
        } catch (WebApplicationException e) {
            return Response.status(e.getResponse().getStatus())
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating journal")
                    .build();
        }
    }
    @GET
    @UnitOfWork
    @Path("/{username}") // Define the path to get all journal titles by username
    public Response getAllJournalTitlesByUsername(@PathParam("username") String username) {
        Optional<User> userOptional = userDao.findByUsername(username);

        if (userOptional.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("User not found with username: " + username)
                    .build();
        }

        User user = userOptional.get();
        List<String> journalTitles = journalDao.getJournalTitlesByUser(user);

        return Response.ok(journalTitles).build();
    }


}