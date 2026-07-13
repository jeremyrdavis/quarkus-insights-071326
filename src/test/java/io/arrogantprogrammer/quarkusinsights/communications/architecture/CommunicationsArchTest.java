package io.arrogantprogrammer.quarkusinsights.communications.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Communications bounded-context isolation and technology hygiene (spec §18.8).
 */
@AnalyzeClasses(packages = "io.arrogantprogrammer", importOptions = ImportOption.DoNotIncludeTests.class)
class CommunicationsArchTest {

    @ArchTest
    static final ArchRule communications_domain_is_free_of_frameworks = noClasses()
            .that().resideInAPackage("..communications.domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "jakarta.persistence..",
                    "jakarta.ws.rs..",
                    "jakarta.enterprise..",
                    "io.quarkus.mailer..",
                    "io.quarkus.scheduler..")
            .because("the communications domain must not depend on JPA, JAX-RS, CDI, Mailer, or the scheduler");

    @ArchTest
    static final ArchRule communications_reaches_cfp_only_through_published_events = noClasses()
            .that().resideInAPackage("..communications..")
            .should().dependOnClassesThat(
                    resideInAPackage("..cfp..").and(not(resideInAPackage("..cfp.domain.events.."))))
            .because("communications may depend on cfp only through cfp.domain.events");

    @ArchTest
    static final ArchRule cfp_does_not_depend_on_communications = noClasses()
            .that().resideInAPackage("..cfp..")
            .should().dependOnClassesThat().resideInAPackage("..communications..")
            .because("the cfp context must not depend on communications");
}
