package northcoders.hibernaterevisitedjavafx.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Department {

    @Id
    @GeneratedValue
    public long id;

    public String name;

    @OneToMany(mappedBy = "department")
    public List<Staff> staff;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "university_id")
    public University university;

    public Department() {
    }

}
