package io.arrogantprogrammer.quarkusinsights.cfp.domain;

import io.arrogantprogrammer.quarkusinsights.cfp.application.PresenterDTO;

public class Presenter {

    private EmailAddress email;
    private String firstName;
    private String lastName;

    public static Presenter create() {
        return new Presenter();
    }

    public Presenter withEmail(EmailAddress email) {
        this.email = email;
        return this;
    }

    public Presenter withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public Presenter withLastName(String lastName) {
        this.lastName = lastName;
        return this;
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

    public PresenterDTO toDTO() {
        return new PresenterDTO(email, firstName, lastName);
    }
}
