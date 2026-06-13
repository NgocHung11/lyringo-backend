package com.lyringo.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.lyringo", importOptions = ImportOption.DoNotIncludeTests.class)
class CleanArchitectureTest {

  @ArchTest
  static final ArchRule domain_should_not_depend_on_frameworks =
      noClasses()
          .that()
          .resideInAnyPackage("..domain..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage(
              "org.springframework..", "jakarta.persistence..", "jakarta.servlet..", "javax.servlet..");

  @ArchTest
  static final ArchRule application_should_not_depend_on_frameworks =
      noClasses()
          .that()
          .resideInAnyPackage("..application..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage(
              "org.springframework..", "jakarta.persistence..", "jakarta.servlet..", "javax.servlet..");

  @ArchTest
  static final ArchRule application_should_not_depend_on_infrastructure =
      noClasses()
          .that()
          .resideInAnyPackage("..application..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("..infrastructure..");

  @ArchTest
  static final ArchRule application_should_not_depend_on_interface_adapters =
      noClasses()
          .that()
          .resideInAnyPackage("..application..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("..interfaceadapter..");

  @ArchTest
  static final ArchRule rest_controllers_should_live_in_interface_adapter_package =
      classes()
          .that()
          .haveSimpleNameEndingWith("Controller")
          .should()
          .resideInAnyPackage("..interfaceadapter.rest..");
}
