package com.matkon.gamelog.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArchitectureTest {

    private JavaClasses importedClasses;

    @BeforeAll
    void setUp() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .withImportOption(location -> {
                    String path = location.asURI().toString();
                    return !path.contains("com/matkon/gamelog/legacy");
                })
                .importPackages("com.matkon.gamelog");
    }

    @Test
    @DisplayName("Architecture layers should be separated")
    void domain_should_be_separated() {
        ArchTestConfiguration.DOMAIN_SHOULD_BE_FREE_FROM_API_AND_INFRASTRUCTURE.check(importedClasses);
        ArchTestConfiguration.API_SHOULD_BE_FREE_FROM_INFRASTRUCTURE.check(importedClasses);
        ArchTestConfiguration.INFRASTRUCTURE_SHOULD_BE_FREE_FROM_API.check(importedClasses);
    }

//    @Test
//    @DisplayName("Domain should not be used in controller request or response")
//    void domain_should_not_be_used_in_controller_request_or_response() {
//        ArchTestConfiguration.DTO_SHOULD_BE_FREE_FROM_DOMAIN_AND_INFRASTRUCTURE.check(importedClasses);
//        ArchTestConfiguration.CONTROLLERS_SHOULD_BE_FREE_FROM_DOMAIN.check(importedClasses);
//    }
}