package com.flipkart;
import com.flipkart.core.Journal;
import com.flipkart.core.User;
import com.flipkart.db.JournalDao;
import com.flipkart.db.UserDao;
import com.flipkart.exception.ConstraintViolationExceptionMapper;
import com.flipkart.health.HealthCheck;
import com.flipkart.resources.JournalResource;
import com.flipkart.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class JournalApplication extends Application<configuartion> {

    public static void main(String[] args) throws Exception
    {
        new JournalApplication().run(args);
    }

    @Override
    public String getName()
    {
        return "hello-world";
    }

    private final HibernateBundle<configuartion> hibernate = new HibernateBundle<configuartion>(User.class, Journal.class)
    {
        @Override
        public DataSourceFactory getDataSourceFactory(configuartion configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    @Override
    public void initialize(Bootstrap<configuartion> bootstrap)
    {
        bootstrap.addBundle(hibernate);
    }

    @Override
    public void run(configuartion config, Environment environment)
    {
        final UserDao dao = new UserDao(hibernate.getSessionFactory());
        final JournalDao journalDao = new JournalDao(hibernate.getSessionFactory());
        environment.jersey().register(new UserResource(dao));
        environment.jersey().register(hibernate);
        environment.jersey().register(new JournalResource(journalDao,dao));
        environment.jersey().register(new HealthCheck());
        environment.jersey().register(new ConstraintViolationExceptionMapper());

     }
}
