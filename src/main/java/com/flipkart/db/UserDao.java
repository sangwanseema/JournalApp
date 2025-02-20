package com.flipkart.db;


import com.flipkart.core.User;
import io.dropwizard.hibernate.AbstractDAO;
import io.dropwizard.hibernate.UnitOfWork;
import org.hibernate.SessionFactory;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

public class UserDao extends AbstractDAO<User>
{
    //SessionFactory is used to create Hibernate sessions for database transactions.
    public UserDao(SessionFactory factory)
    {
        // method to initialize the AbstractDAO with the Hibernate session
        super(factory);
    }
    public Optional<User> findByUsername(String username)
    {
        return Optional.ofNullable((User) currentSession()
                .createQuery("FROM User WHERE username = :username")
                .setParameter("username", username)
                .uniqueResult());
    }
    // Save a user with proper error handling
    public User saveUser(User user)
    {
        try
        {   //This method persists (saves) a User object to the database.
            //which is provided by AbstractDAO, to insert or update the user in the database.
            return persist(user);
        }
        catch (ConstraintViolationException  e)
        {
            // Assuming "username" is the column that might be duplicated
            if (e.getMessage().contains("username"))
            {
                throw new WebApplicationException("Username already exists", Response.Status.CONFLICT);
            }
            throw new WebApplicationException("Database error", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    // Get a user by ID
    public Optional<User> getUserById(Long id)
    {
        return Optional.ofNullable(get(id)); // Fetch user by ID
    }

    // Delete a user from the database
    public void deleteUser(User user)
    {
        currentSession().delete(user); // Delete user from the database
    }

    // Get all users from the database
    public List<User> getAllUsers()
    {
        TypedQuery<User> query = currentSession().createQuery("FROM User", User.class);
        return query.getResultList();
    }

}
