package io.arrogantprogrammer.quarkusinsights.cfp.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "presenter")
public class PresenterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "presenter_seq")
    @SequenceGenerator(name = "presenter_seq", sequenceName = "presenter_sequence")
    private Long id;

    private String email;

    private String firstName;

    private String lastName;

    public PresenterEntity() {
    }

    public PresenterEntity(String email, String firstName, String lastName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
