package io.arrogantprogrammer.quarkusinsights.cfp.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Service-layering discipline (per the ddd-services skill). A service class
 * must declare its layer in its name — {@code *ApplicationService} (use-case
 * orchestration, in {@code application} or in the {@code admin} aggregator) or
 * {@code *DomainService} (stateless domain logic, in {@code domain}); a bare
 * {@code *Service} is the layer-confusion smell.
 */
@AnalyzeClasses(packages = "com.pickweasel", importOptions = ImportOption.DoNotIncludeTests.class)
class ServiceArchTest {

    @ArchTest
    static final ArchRule services_declare_their_layer_in_their_name = classes()
            .that().haveSimpleNameEndingWith("Service").and().areNotInterfaces()
            .should().haveSimpleNameEndingWith("ApplicationService")
            .orShould().haveSimpleNameEndingWith("DomainService")
            .as("service classes should be named *ApplicationService or *DomainService")
            .because("a bare *Service hides which layer owns the logic (ddd-services)");

    @ArchTest
    static final ArchRule application_services_live_in_application = classes()
            .that().haveSimpleNameEndingWith("ApplicationService").and().areNotInterfaces()
            .should().resideInAnyPackage("..application..", "com.pickweasel.admin..")
            .as("application services should reside in the application package (or the admin aggregator)")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule domain_services_live_in_domain = classes()
            .that().haveSimpleNameEndingWith("DomainService").and().areNotInterfaces()
            .should().resideInAPackage("..domain..")
            .as("domain services should reside in the domain package")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule domain_services_do_not_depend_on_repositories = noClasses()
            .that().haveSimpleNameEndingWith("DomainService")
            .should().dependOnClassesThat().haveSimpleNameEndingWith("Repository")
            .as("domain services should not inject repositories")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule domain_services_do_not_use_cdi_events_or_orm = noClasses()
            .that().haveSimpleNameEndingWith("DomainService")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "jakarta.enterprise.event..", "jakarta.persistence..")
            .as("domain services should not use CDI events or ORM")
            .allowEmptyShould(true);
}
