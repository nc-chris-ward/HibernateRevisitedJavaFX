module northcoders.hibernaterevisitedjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires org.reflections;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires com.h2database;


    opens northcoders.hibernaterevisitedjavafx to javafx.fxml, org.hibernate.orm.core;
    opens northcoders.hibernaterevisitedjavafx.model to org.hibernate.orm.core;
    exports northcoders.hibernaterevisitedjavafx;
    exports northcoders.hibernaterevisitedjavafx.model;
}