package northcoders.hibernaterevisitedjavafx.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class University {

    @Id
    @GeneratedValue
    public long id;

    public String name;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Department> departments;

    public University() {
    }
}
