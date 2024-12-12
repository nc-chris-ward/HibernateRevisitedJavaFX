package northcoders.hibernaterevisitedjavafx;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import northcoders.hibernaterevisitedjavafx.config.HibernateConfig;
import northcoders.hibernaterevisitedjavafx.model.Department;
import northcoders.hibernaterevisitedjavafx.model.University;
import northcoders.hibernaterevisitedjavafx.repository.H2Repository;
import northcoders.hibernaterevisitedjavafx.view.ClassMenu;
import org.h2.security.auth.impl.JaasCredentialsValidator;
import org.h2.tools.Server;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;


public class RuntimeHibernateApplication extends Application {

    public static void main(String[] args) {
        startH2Console();
        customPersist();
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Entity Class List");
        primaryStage.setScene(new ClassMenu(primaryStage));

        Screen secondaryScreen = Screen.getScreens().get(1);
        Rectangle2D bounds = secondaryScreen.getVisualBounds();

        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());

        primaryStage.show();

    }

    public static void startH2Console() {
        try {
            Server server = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082");
            server.start();
            System.out.println("H2 Console is running at http://localhost:8082");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void customPersist() {

        SessionFactory sessionFactory = HibernateConfig.getSessionFactory();
        sessionFactory.getSchemaManager().exportMappedObjects(true);

        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        Department department = new Department();
        department.name = "dbjak";

        University uni = new University();
        uni.name = "EBJK";

        uni.departments = new ArrayList<>();
        uni.departments.add(department);

        department.university = uni;

        try {
            transaction = session.beginTransaction();

            // IN OUR SESSION

            session.persist(uni);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            session.close();
        }

        // OUTSIDE OUR SESSION

    }

}