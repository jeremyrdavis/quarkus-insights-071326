package io.arrogantprogrammer.quarkusinsights.cfp.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Domain-event placement. Everything that is a domain event implements the
 * {@code shared.domain.DomainEvent} marker and lives in a {@code domain/events}
 * package, and every {@code *Event} type that lives there is in fact a domain
 * event. (Note: {@code ChallengeEvent} is a value object describing a sporting
 * fixture, not a domain event, and correctly lives under {@code valueobjects}.)
 * All HARD.
 */
@AnalyzeClasses(packages = "com.pickweasel", importOptions = ImportOption.DoNotIncludeTests.class)
class DomainEventArchTest {

    private static final String DOMAIN_EVENT = "com.pickweasel.shared.domain.DomainEvent";

    @ArchTest
    static final ArchRule domain_events_reside_in_events_packages = classes()
            .that().implement(DOMAIN_EVENT)
            .should().resideInAPackage("..domain.events..")
            .because("domain events belong to their bounded context's domain/events package");

    @ArchTest
    static final ArchRule event_types_in_events_packages_are_domain_events = classes()
            .that().resideInAPackage("..domain.events..").and().haveSimpleNameEndingWith("Event")
            .should().implement(DOMAIN_EVENT)
            .because("a *Event in a domain/events package must implement the DomainEvent marker");
}
