package io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;

public class Presenter {

    private EmailAddress email;
    private String firstName;
    private String lastName;

    public static Presenter create(EmailAddress email, String firstName, String lastName) {
        Presenter presenter = new Presenter();
        presenter.email = email;
        presenter.firstName = firstName;
        presenter.lastName = lastName;
        return presenter;
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
