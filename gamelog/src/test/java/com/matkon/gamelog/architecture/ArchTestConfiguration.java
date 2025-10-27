package com.matkon.gamelog.architecture;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.lang.ArchRule;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static java.util.function.Predicate.not;

public class ArchTestConfiguration {

    public static final String BASE_PACKAGE = "com.matkon.gamelog";
    public static final String API_PACKAGE = "com.matkon.gamelog.api..";
    public static final String DOMAIN_PACKAGE = "com.matkon.gamelog.domain..";
    public static final String DOMAIN_PACKAGE_REGEX = ".*com.matkon.gamelog..*domain.*";
    public static final String INFRASTRUCTURE_PACKAGE = "com.matkon.gamelog.infrastructure..";
    public static final String INFRASTRUCTURE_INTEGRATION_PACKAGE = "com.matkon.gamelog.infrastructure..integration..";

    public static final ArchRule DOMAIN_SHOULD_BE_FREE_FROM_API_AND_INFRASTRUCTURE = noClasses()
            .that().resideInAPackage(DOMAIN_PACKAGE)
            .should().dependOnClassesThat().resideInAnyPackage(API_PACKAGE, INFRASTRUCTURE_PACKAGE)
            .because("domain should be free from api and infrastructure")
            .allowEmptyShould(true);

    public static final ArchRule API_SHOULD_BE_FREE_FROM_INFRASTRUCTURE = noClasses()
            .that().resideInAPackage(API_PACKAGE)
            .should().dependOnClassesThat().resideInAPackage(INFRASTRUCTURE_PACKAGE)
            .because("api should be free from infrastructure")
            .allowEmptyShould(true);

    public static final ArchRule INFRASTRUCTURE_SHOULD_BE_FREE_FROM_API = noClasses()
            .that(describe("reside in infrastructure but not in integration package",
                    resideInAPackage(INFRASTRUCTURE_PACKAGE)
                            .and(not(resideInAPackage(INFRASTRUCTURE_INTEGRATION_PACKAGE)))))
            .should().dependOnClassesThat().resideInAPackage(API_PACKAGE)
            .because("infrastructure should be free from api, except integration packages")
            .allowEmptyShould(true);

    public static final ArchRule DTO_SHOULD_BE_FREE_FROM_DOMAIN_AND_INFRASTRUCTURE = noClasses()
            .that().resideInAPackage(API_PACKAGE)
            .and(new DescribedPredicate<>("class does not contain 'DtoMapper'") {
                @Override
                public boolean test(final JavaClass javaClass) {
                    return !javaClass.getSimpleName().contains("DtoMapper");
                }
            })
            .should().dependOnClassesThat(
                    JavaClass.Predicates.resideInAnyPackage(DOMAIN_PACKAGE, INFRASTRUCTURE_PACKAGE)
                            .and(new DescribedPredicate<>("class is not enum") {
                                @Override
                                public boolean test(final JavaClass javaClass) {
                                    return !javaClass.isEnum();
                                }
                            }))
            .because("dtos should be free from domain")
            .allowEmptyShould(true);


    public static final ArchRule CONTROLLERS_SHOULD_BE_FREE_FROM_DOMAIN = noClasses()
            .that().resideInAPackage(API_PACKAGE)
            .and().haveSimpleNameEndingWith("Controller")
            .should().callMethodWhere(new DescribedPredicate<>("controller methods don't accept domain objects and don't return domain objects") {
                @Override
                public boolean test(final JavaMethodCall javaMethodCall) {
                    String methodName = javaMethodCall.getOwner().getName();

                    return Stream.of(javaMethodCall.getOriginOwner().reflect().getDeclaredMethods())
                            .filter(method -> !Modifier.isPrivate(method.getModifiers()))
                            .map(Method::toGenericString)
                            .filter(genericName -> genericName.contains(methodName))
                            .anyMatch(genericName -> genericName.matches(DOMAIN_PACKAGE_REGEX));
                }
            })
            .because("controllers should be free from domain")
            .allowEmptyShould(true);
}