package io.arrogantprogrammer.quarkusinsights.cfp.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;

/**
 * Value-object immutability. Value objects (records and enums) are immutable:
 * no setters and no non-final instance state. Operations that "modify" a value
 * return a new instance. All HARD.
 */
@AnalyzeClasses(packages = "io.arrogantprogrammer", importOptions = ImportOption.DoNotIncludeTests.class)
class ValueObjectArchTest {

    @ArchTest
    static final ArchRule value_objects_have_no_setters = noMethods()
            .that().areDeclaredInClassesThat().resideInAPackage("..domain.valueobjects..")
            .should().haveNameMatching("set[A-Z].*")
            .because("value objects are immutable")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule value_object_fields_are_final = fields()
            .that().areDeclaredInClassesThat().resideInAPackage("..domain.valueobjects..")
            .and().areNotStatic()
            .should().beFinal()
            .because("value objects are immutable; instance state must be final")
            .allowEmptyShould(true);
}
