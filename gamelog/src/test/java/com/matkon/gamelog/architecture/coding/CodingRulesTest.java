package com.matkon.gamelog.architecture.coding;

import com.matkon.gamelog.architecture.ArchTestConfiguration;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;

@AnalyzeClasses(
        packages = ArchTestConfiguration.BASE_PACKAGE,
        importOptions = {ExcludeLegacyFolder.class}
)
public class CodingRulesTest {

    @ArchTest
    private final ArchRule NO_JODATIME = GeneralCodingRules.NO_CLASSES_SHOULD_USE_JODATIME;

    @ArchTest
    private final ArchRule NO_JAVA_UTIL_LOGGING = GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;
}