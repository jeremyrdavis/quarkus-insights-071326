package io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;

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

}
