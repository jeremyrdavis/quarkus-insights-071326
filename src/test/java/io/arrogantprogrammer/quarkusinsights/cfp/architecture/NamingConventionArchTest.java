package io.arrogantprogrammer.quarkusinsights.cfp.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.eclipse.microprofile.config.ConfigProvider;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * House naming/placement conventions (descriptive — these lock in the codebase's
 * deliberate, consistent style rather than the generic skill template). JAX-RS
 * server resources are {@code *Endpoint} classes in {@code infrastructure}; DTOs
 * and exception mappers also live in {@code infrastructure}; repository
 * interfaces live in {@code persistence} or {@code domain/repositories}.
 * MicroProfile {@code @RegisterRestClient} interfaces also carry {@code @Path}
 * but are outbound clients, so the endpoint rule is scoped to non-interfaces.
 * The {@code admin} module is a flat operator-facing aggregator (e.g.
 * {@code admin.challenges}, {@code admin.users}) that does not use the
 * per-context {@code infrastructure} layout, so it is an accepted location for
 * its {@code *Endpoint} and {@code *DTO} types. All HARD.
 */
@AnalyzeClasses(packages = "io.arrogantprogrammer", importOptions = ImportOption.DoNotIncludeTests.class)
class NamingConventionArchTest {

    private static final String ADMIN_PACKAGE = ConfigProvider.getConfig().getValue("base-package", String.class) + ".admin..";

    @ArchTest
    static final ArchRule jaxrs_resources_are_endpoints_in_infrastructure = classes()
            .that().areAnnotatedWith("jakarta.ws.rs.Path").and().areNotInterfaces()
            .should().haveSimpleNameEndingWith("Resource")
            .andShould().resideInAnyPackage("..infrastructure..", ADMIN_PACKAGE)
            .because("JAX-RS server resources follow the *Resource convention in the infrastructure layer (or the admin aggregator)");

    @ArchTest
    static final ArchRule dtos_live_in_infrastructure = classes()
            .that().haveSimpleNameEndingWith("DTO")
            .should().resideInAnyPackage("..application..", ADMIN_PACKAGE)
            .because("DTOs are an application/transport concern in this codebase (or the admin aggregator)");

    @ArchTest
    static final ArchRule exception_mappers_are_providers_in_infrastructure = classes()
            .that().haveSimpleNameEndingWith("ExceptionMapper")
            .should().beAnnotatedWith("jakarta.ws.rs.ext.Provider")
            .andShould().resideInAPackage("..infrastructure..")
            .because("exception mappers are JAX-RS @Provider components in the infrastructure layer")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule repository_interfaces_live_in_persistence_or_domain = classes()
            .that().areInterfaces().and().haveSimpleNameEndingWith("Repository")
            .should().resideInAnyPackage("..persistence..", "..domain.repositories..")
            .because("repository ports live either in persistence or in domain/repositories")
            .allowEmptyShould(true);
}
