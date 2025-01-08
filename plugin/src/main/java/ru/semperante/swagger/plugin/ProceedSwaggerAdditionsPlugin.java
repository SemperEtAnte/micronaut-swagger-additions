package ru.semperante.swagger.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskProvider;

public class ProceedSwaggerAdditionsPlugin implements Plugin<Project> {
   @Override
   public void apply(Project project) {
      ProceedReservedExceptionsTask task = project.getTasks().create("swaggerizeReservedExceptions", ProceedReservedExceptionsTask.class);
      TaskProvider<Task> p = project.getTasks().named("compileJava");
      if (p.isPresent()) {
         p.get().finalizedBy(task);
      }
   }
}