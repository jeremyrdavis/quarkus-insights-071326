package io.arrogantprogrammer.quarkusinsights.cfp.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Persistence-technology guardrails. PickWeasel persists exclusively via Cloud
 * Firestore; JPA/Hibernate/Panache must never be introduced, and Firestore
 * repository implementations belong in the persistence layer. All HARD.
 */
@AnalyzeClasses(packages = "com.pickweasel", importOptions = ImportOption.DoNotIncludeTests.class)
class PersistenceTechnologyArchTest {

    @ArchTest
    static final ArchRule no_jpa_anywhere = noClasses()
            .should().dependOnClassesThat().resideInAPackage("jakarta.persistence..")
            .because("PickWeasel persists via Cloud Firestore; JPA/Hibernate must never be introduced");

    @ArchTest
    static final ArchRule no_panache_anywhere = noClasses()
            .should().dependOnClassesThat().resideInAnyPackage(
                    "io.quarkus.hibernate.orm.panache..",
                    "io.quarkus.hibernate.reactive.panache..")
            .because("this codebase does not use Panache");

    @ArchTest
    static final ArchRule firestore_repository_implementations_live_in_persistence = classes()
            .that().haveSimpleNameStartingWith("Firestore").and().haveSimpleNameEndingWith("Repository")
            .should().resideInAPackage("..persistence..")
            .because("Firestore repository implementations are an infrastructure concern in the persistence layer");
}
