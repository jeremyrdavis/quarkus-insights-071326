# TODO

## io.arrogantprogrammer.quarkusinsights.cfp.infrastructure

- [x] Add Jakarta Validation to the PresenterParameters record
- [x] Add a GET endpoint to retrieve a presenter by email
- [x] Add a PUT endpoint to update a presenter by email
- [x] Add a DELETE endpoint to remove a presenter by email

## io.arrogantprogrammer.quarkusinsights.cfp.domain

- [x] Add a BuilderMethod to the Submitter class with email, firstName, and lastName parameters

## general
- Add UUIDs to created objects, return a 201 Created response with the location of the created object

## validation
- Validity check for emails
-- where to check if email already exists? A: Domain Service