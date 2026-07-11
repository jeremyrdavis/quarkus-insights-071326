package io.arrogantprogrammer.quarkusinsights.cfp.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.properties.HasName.Predicates.nameMatching;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;

/**
 * Aggregate-root encapsulation. Aggregates expose behaviour through named domain
 * methods, never through public mutable state or setters. JPA/Panache absence is
 * enforced globally by {@link PersistenceTechnologyArchTest}. All HARD.
 */
@AnalyzeClasses(packages = "io.arrogantprogrammer", importOptions = ImportOption.DoNotIncludeTests.class)
class AggregateArchTest {

    @ArchTest
    static final ArchRule aggregates_have_no_public_instance_fields = fields()
            .that().areDeclaredInClassesThat().resideInAPackage("..domain.aggregates..")
            .and().areNotStatic()
            .should().notBePublic()
            .because("aggregate state is mutated through named domain methods, not exposed fields");

    @ArchTest
    static final ArchRule aggregates_have_no_setters = noMethods()
            .that().areDeclaredInClassesThat().resideInAPackage("..domain.aggregates..")
            .should().haveNameMatching("set[A-Z].*")
            .because("aggregates enforce invariants via intention-revealing methods, not setters");

    @ArchTest
    static final ArchRule aggregates_do_not_return_dtos = methods()
            .that().areDeclaredInClassesThat().resideInAPackage("..domain.aggregates..")
            .should().notHaveRawReturnType(resideInAPackage("..application.."))
            .andShould().notHaveRawReturnType(nameMatching(".*[Dd][Tt][Oo].*"))
            .because("aggregates should not depend on DTOs; they should return domain objects or primitives");
}
