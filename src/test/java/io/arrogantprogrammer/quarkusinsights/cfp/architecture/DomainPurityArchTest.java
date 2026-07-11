package io.arrogantprogrammer.quarkusinsights.cfp.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Domain dependency hygiene. The whole domain — the model (aggregates, value
 * objects, events), the {@code *DomainService} domain services, and the
 * supporting commands / ports / exceptions in {@code domain/services} — must
 * not depend on {@code infrastructure} or {@code persistence}, must not pull
 * in ORM, JAX-RS, or Firestore types, and may rely only on its own domain
 * abstractions.
 */
@AnalyzeClasses(packages = "com.pickweasel", importOptions = ImportOption.DoNotIncludeTests.class)
class DomainPurityArchTest {

    @ArchTest
    static final ArchRule domain_model_does_not_depend_on_infrastructure_or_persistence = noClasses()
            .that().resideInAnyPackage("..domain.aggregates..", "..domain.valueobjects..", "..domain.events..")
            .should().dependOnClassesThat().resideInAnyPackage("..infrastructure..", "..persistence..")
            .because("the domain model must stay free of infrastructure and persistence concerns");

    @ArchTest
    static final ArchRule domain_is_free_of_web_and_orm_frameworks = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "jakarta.persistence..", "jakarta.ws.rs..", "com.google.cloud.firestore..")
            .because("domain logic must not import ORM, JAX-RS, or Firestore types");

    @ArchTest
    static final ArchRule domain_does_not_depend_on_infrastructure = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
            .as("domain classes should not depend on infrastructure")
            .because("domain logic must not reach into infrastructure (DTOs, dispatchers, adapters)");

    @ArchTest
    static final ArchRule domain_does_not_depend_on_persistence = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("..persistence..")
            .as("domain classes should not depend on persistence")
            .because("domain logic must not reach into the persistence layer");
}
