package com.flipkart.db;

import com.flipkart.core.Journal;
import com.flipkart.core.User;
import io.dropwizard.hibernate.AbstractDAO;
import io.dropwizard.hibernate.UnitOfWork;
import org.hibernate.SessionFactory;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
public class JournalDao  extends AbstractDAO<Journal>
{
    public JournalDao(SessionFactory sessionFactory)
    {
        super(sessionFactory);
    }
    @UnitOfWork
    public Journal saveJournal(Journal journal)
    {

        try
        {   //This method persists (saves) a Journal object to the database.
            //which is provided by AbstractDAO, to insert or update the user in the database.
            return persist(journal);
        }
        catch (ConstraintViolationException e)
        {
            // Assuming "username" is the column that might be duplicated
            if (e.getMessage().contains("Duplicate entry"))
            {
                throw new WebApplicationException("title already exists", Response.Status.CONFLICT);
            }
            throw new WebApplicationException("Database error", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteJournal(Journal journal)
    {
        currentSession().delete(journal);
    }


   public List<Journal> getAllJournals()
   {
        TypedQuery<Journal> query = currentSession().createQuery("from Journal", Journal.class);
        return query.getResultList();
   }
    public Optional<Journal> findByTitle(String title)
    {
        return Optional.ofNullable((Journal) currentSession()
                .createQuery("FROM Journal WHERE title = :title")
                .setParameter("title", title)
                .uniqueResult());
    }
    public List<String> getJournalTitlesByUser(User user)
    {
        return currentSession()
                .createQuery("SELECT j.title FROM Journal j WHERE j.user = :user", String.class)
                .setParameter("user", user)
                .getResultList();
    }

}
