package com.example.spring_boot;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class MyArchitectureTest {
    @Test
    public void some_architecture_rule() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("com.example.spring_boot");

        ArchRule rule = classes()
                .that().haveNameMatching(".*Controller")
                .should().beAnnotatedWith(Controller.class);
                // see next section

        rule.check(importedClasses);
    }

    @Test
    public void servicesImplementingTestServiceMustHaveCorrespondingRepository() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("com.example.spring_boot");

        ArchCondition<JavaClass> haveCorrespondingRepository =
                new ArchCondition<>("have a corresponding repository implementing TestRepository") {
                    @Override
                    public void check(JavaClass serviceClass, ConditionEvents events) {
                        String serviceClassName = serviceClass.getSimpleName();
                        String expectedRepositoryName = serviceClassName.replace("Service", "Repository");
                        System.out.println(expectedRepositoryName);

                        boolean hasMatchingRepository = importedClasses.stream()
                                .filter(repositoryClass -> repositoryClass.getSimpleName().equals(expectedRepositoryName))
                                .anyMatch(repositoryClass -> repositoryClass.isAssignableTo(TestRepository.class));

                        if (!hasMatchingRepository) {
                            String message = String.format("Service %s does not have a corresponding repository implementing TestRepository",
                                    serviceClass.getName());
                            events.add(SimpleConditionEvent.violated(serviceClass, message));
                        }
                    }
                };

        ArchRule rule = classes()
                .that().implement(TestService.class)
                .should(haveCorrespondingRepository);

        rule.check(importedClasses);
    }
}
