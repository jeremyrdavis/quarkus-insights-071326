package io.arrogantprogrammer.quarkusinsights.cfp.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;

import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

/**
 * Persistence-technology guardrails.
 * The application persists exclusively via JPA/Hibernate/Panache.
 * All PanacheRepository repository implementations belong in the persistence layer.
 * All HARD.
 */
@AnalyzeClasses(packages = "io.arrogantprogrammer", importOptions = ImportOption.DoNotIncludeTests.class)
class PersistenceTechnologyArchTest {

    @ArchTest
    static final ArchRule jpa_annotated_classes_must_reside_in_persistence_package = classes()
            .that().areAnnotatedWith(Entity.class)
            .or().areAnnotatedWith(MappedSuperclass.class)
            .or().areAnnotatedWith(Embeddable.class)
            .should().resideInAPackage("..persistence..")
            .because("JPA entities, mapped superclasses, and embeddables are persistence concerns and must reside in the persistence package");

    @ArchTest
    static final ArchRule jpa_annotated_classes_only_allow_public_accessors = methods()
            .that().areDeclaredInClassesThat().areAnnotatedWith(Entity.class)
            .or().areDeclaredInClassesThat().areAnnotatedWith(MappedSuperclass.class)
            .or().areDeclaredInClassesThat().areAnnotatedWith(Embeddable.class)
            .should().bePublic()
            .because("JPA entities, mapped superclasses, and embeddables should only be accessed via public methods");
}
