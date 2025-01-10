package ru.semperante;

import io.micronaut.aot.core.AOTContext;
import io.micronaut.aot.core.AOTModule;
import io.micronaut.aot.core.codegen.AbstractCodeGenerator;
import io.micronaut.core.annotation.NonNull;

@AOTModule(id = "ru.semperante.swagger")
public class SwaggerMicronautAotGenerator extends AbstractCodeGenerator {
   @Override
   public void generate(@NonNull AOTContext context) {
      System.out.println("I CALLEDDDDD!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
   }
}
