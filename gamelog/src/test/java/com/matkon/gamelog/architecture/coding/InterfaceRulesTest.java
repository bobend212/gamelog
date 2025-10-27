package com.matkon.gamelog.architecture.coding;

import com.matkon.gamelog.architecture.ArchTestConfiguration;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = ArchTestConfiguration.BASE_PACKAGE,
        importOptions = {ExcludeLegacyFolder.class}
)
public class InterfaceRulesTest {

    @ArchTest
    public void interfaces_should_not_have_names_ending_with_the_word_interface(final JavaClasses classes) {
        noClasses().that().areInterfaces()
                .should().haveNameMatching(".*Interface")
                .allowEmptyShould(true)
                .check(classes);
    }

    @ArchTest
    public void interfaces_should_not_have_simple_class_names_ending_with_the_word_interface(final JavaClasses classes) {
        noClasses().that().areInterfaces()
                .should().haveSimpleNameContaining("Interface")
                .allowEmptyShould(true)
                .check(classes);
    }

    @ArchTest
    public void interfaces_must_not_be_placed_in_implementation_packages(final JavaClasses classes) {
        noClasses().that().resideInAPackage("..impl..")
                .should().beInterfaces()
                .allowEmptyShould(true)
                .check(classes);
    }

}
