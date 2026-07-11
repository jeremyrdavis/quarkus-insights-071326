package io.arrogantprogrammer.quarkusinsights.cfp.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "presenter")
public class PresenterEntity {

    @Id
    private java.util.UUID id;

    private String email;

    private String firstName;

    private String lastName;

    public PresenterEntity() {
    }

    public PresenterEntity(java.util.UUID id, String email, String firstName, String lastName) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public java.util.UUID getId() {
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
