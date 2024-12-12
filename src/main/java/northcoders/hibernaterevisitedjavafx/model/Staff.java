package northcoders.hibernaterevisitedjavafx.model;

import jakarta.persistence.*;

@Entity
public class Staff {

    @Id
    @GeneratedValue
    long id;

    public String name;

    @ManyToOne
    public Department department;

    public Staff() {
    }

}
