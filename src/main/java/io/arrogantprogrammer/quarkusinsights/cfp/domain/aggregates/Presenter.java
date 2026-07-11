package io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;

public class Presenter {

    private java.util.UUID id;
    private EmailAddress email;
    private String firstName;
    private String lastName;

    public Presenter() {
    }

    public Presenter(java.util.UUID id, EmailAddress email, String firstName, String lastName) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static Presenter create(EmailAddress email, String firstName, String lastName) {
        Presenter presenter = new Presenter();
        presenter.id = java.util.UUID.randomUUID();
        presenter.email = email;
        presenter.firstName = firstName;
        presenter.lastName = lastName;
        return presenter;
    }

    public java.util.UUID getId() {
        return id;
    }

    public EmailAddress getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

}
