package com.matkon.gamelog.architecture.coding;

import com.matkon.gamelog.architecture.ArchTestConfiguration;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(
        packages = ArchTestConfiguration.BASE_PACKAGE,
        importOptions = {ExcludeLegacyFolder.class}
)
public class ExceptionTest {

    @ArchTest
    public void exceptions_should_not_have_default_constructor(final JavaClasses classes) {
        classes().that(extendsException)
                .should(haveNotDefaultConstructor)
                .allowEmptyShould(true)
                .check(classes);
    }

    @ArchTest
    public void exceptions_should_be_suffixed(final JavaClasses classes) {
        classes().that(extendsException)
                .should().haveSimpleNameEndingWith("Exception")
                .allowEmptyShould(true)
                .check(classes);
    }

    private final DescribedPredicate<JavaClass> extendsException = new DescribedPredicate<>("extends java.lang.Exception") {
        @Override
        public boolean test(final JavaClass item) {
            return item.getAllRawSuperclasses().stream().anyMatch(i -> "java.lang.Exception".equals(i.getName()));
        }
    };

    private final ArchCondition<JavaClass> haveNotDefaultConstructor = new ArchCondition<>("not have default public constructor") {
        @Override
        public void check(final JavaClass item, final ConditionEvents events) {
            boolean result = item.getConstructors()
                    .stream()
                    .anyMatch(c -> c.getRawParameterTypes().isEmpty() && c.getModifiers().contains(JavaModifier.PUBLIC));

            if (result) {
                events.add(SimpleConditionEvent.violated(item, String.format("Class %s should not have default constructor", item.getName())));
            }
        }
    };
}
