package northcoders.hibernaterevisitedjavafx.config;

import jakarta.persistence.Entity;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.reflections.Reflections;

import java.util.Set;

import static java.lang.Boolean.TRUE;
import static org.hibernate.cfg.JdbcSettings.*;

public class HibernateConfig {

    public static final String MODEL_PATH = "northcoders.hibernaterevisitedjavafx.model";

    public static SessionFactory getSessionFactory() {
        Configuration configuration = new Configuration()
                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                .setProperty(JAKARTA_JDBC_URL, "jdbc:h2:mem:db1")
                .setProperty(JAKARTA_JDBC_USER, "sa")
                .setProperty(JAKARTA_JDBC_PASSWORD, "")
                .setProperty("hibernate.hikari.maximumPoolSize", "20")
                .setProperty(SHOW_SQL, TRUE.toString())
                .setProperty(FORMAT_SQL, TRUE.toString())
                .setProperty(HIGHLIGHT_SQL, TRUE.toString());
        return addEntities(configuration).buildSessionFactory();
    }

    private static Configuration addEntities(Configuration configuration) {
        Reflections reflections = new Reflections(MODEL_PATH);
        Set<Class<?>> entityClasses = reflections.getTypesAnnotatedWith(Entity.class);
        for (Class<?> entityClass : entityClasses) {
            configuration.addAnnotatedClass(entityClass);
            System.out.println("HIBERNATE: Entity added - " + entityClass.getName());
        }
        return configuration;
    }

}
