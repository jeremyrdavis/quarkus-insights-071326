package io.arrogantprogrammer.quarkusinsights.cfp.architecture;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import java.util.Set;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static com.tngtech.archunit.library.freeze.FreezingArchRule.freeze;

/**
 * Bounded-context isolation (aspirational; frozen). Two checks:
 * <ol>
 *   <li>the top-level contexts form no dependency cycles;</li>
 *   <li>a context reaches another context only through that context's
 *       <em>public surface</em>: the API ports / commands / exceptions in
 *       {@code <bc>/domain/services}, or the published events in
 *       {@code <bc>/domain/events}. Any cross-context dependency on another
 *       context's {@code application}, {@code domain.aggregates},
 *       {@code domain.valueobjects}, {@code domain.repositories},
 *       {@code infrastructure}, or {@code persistence} is flagged.</li>
 * </ol>
 * Cross-cutting modules ({@code shared}, {@code security}, {@code admin},
 * {@code discovery}) are exempt: {@code shared} holds the {@code DomainEvent}
 * marker and dispatcher used everywhere, and {@code admin} is an aggregator over
 * all contexts. Both rules are frozen so current coupling is baselined while new
 * coupling fails.
 */
@AnalyzeClasses(packages = "com.pickweasel", importOptions = ImportOption.DoNotIncludeTests.class)
class BoundedContextArchTest {

    private static final String BASE = "com.pickweasel.";
    private static final Set<String> CROSS_CUTTING = Set.of("shared", "security", "admin", "discovery");

    /** Cross-cutting modules are intentional hubs/aggregators, not bounded contexts. */
    private static final DescribedPredicate<JavaClass> IN_CROSS_CUTTING = resideInAnyPackage(
            "com.pickweasel.shared..", "com.pickweasel.admin..",
            "com.pickweasel.security..", "com.pickweasel.discovery..");

    /**
     * Published domain events are each context's outbound published language
     * (ADR-0028): observing another context's {@code domain.events} record is
     * the sanctioned subscribe direction, so such dependencies must not count
     * as cycle edges. Without this exemption every request/reply pair — A
     * calls B's {@code domain.services} port while B observes A's events —
     * would read as a cycle.
     */
    private static final DescribedPredicate<JavaClass> IN_PUBLISHED_EVENTS =
            resideInAnyPackage("com.pickweasel..domain.events..");

    @ArchTest
    static final ArchRule contexts_are_free_of_cycles = freeze(
            slices().matching("com.pickweasel.(*)..").should().beFreeOfCycles()
                    .ignoreDependency(IN_CROSS_CUTTING, DescribedPredicate.alwaysTrue())
                    .ignoreDependency(DescribedPredicate.alwaysTrue(), IN_CROSS_CUTTING)
                    .ignoreDependency(DescribedPredicate.alwaysTrue(), IN_PUBLISHED_EVENTS));

    @ArchTest
    static final ArchRule contexts_reach_others_only_through_their_api = freeze(
            classes().should(onlyAccessOtherContextsThroughTheirApi()));

    private static ArchCondition<JavaClass> onlyAccessOtherContextsThroughTheirApi() {
        return new ArchCondition<>(
                "only depend on other bounded contexts through their domain.services API ports or their published domain.events") {
            @Override
            public void check(JavaClass origin, ConditionEvents events) {
                String originContext = contextOf(origin);
                if (originContext == null || CROSS_CUTTING.contains(originContext)) {
                    return;
                }
                for (Dependency dependency : origin.getDirectDependenciesFromSelf()) {
                    JavaClass target = dependency.getTargetClass();
                    String targetContext = contextOf(target);
                    if (targetContext == null
                            || targetContext.equals(originContext)
                            || CROSS_CUTTING.contains(targetContext)) {
                        continue;
                    }
                    if (!isPublicContextSurface(target.getPackageName())) {
                        events.add(SimpleConditionEvent.violated(dependency, dependency.getDescription()));
                    }
                }
            }
        };
    }

    /**
     * The only cross-context targets that count as another context's public surface: the API ports,
     * commands, and exceptions in {@code <bc>.domain.services}, and the published events in
     * {@code <bc>.domain.events}. Everything else (application services, aggregates, value objects,
     * repository interfaces, infrastructure, persistence) is internal.
     */
    private static boolean isPublicContextSurface(String targetPackage) {
        return targetPackage.endsWith(".domain.services")
                || targetPackage.contains(".domain.services.")
                || targetPackage.endsWith(".domain.events")
                || targetPackage.contains(".domain.events.");
    }

    /** First package segment under com.pickweasel, e.g. {@code connection}, {@code sports}, {@code user}. */
    private static String contextOf(JavaClass javaClass) {
        String packageName = javaClass.getPackageName();
        if (!packageName.startsWith(BASE)) {
            return null;
        }
        String remainder = packageName.substring(BASE.length());
        int dot = remainder.indexOf('.');
        return dot < 0 ? remainder : remainder.substring(0, dot);
    }
}
