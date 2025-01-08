package ru.semperante.swagger.plugin;


import org.gradle.api.DefaultTask;
import org.gradle.api.file.Directory;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetOutput;
import org.gradle.api.tasks.TaskAction;
import ru.semperante.swagger.plugin.updater.ISwaggerUpdater;
import ru.semperante.swagger.plugin.updater.JsonSwaggerUpdater;
import ru.semperante.swagger.plugin.updater.YamlSwaggerUpdater;
import ru.semperante.swagger.plugin.utils.HandlersRead;
import ru.semperante.swagger.plugin.utils.UpdaterUtils;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class ProceedReservedExceptionsTask extends DefaultTask {


   @TaskAction
   public void proceedAdditions() {
      try {

         SourceSet mainOutput = getProject().getExtensions().getByType(JavaPluginExtension.class).getSourceSets().findByName("main");
         if (mainOutput == null) {
            getLogger().warn("No main output found");
            return;
         }
         SourceSetOutput out = mainOutput.getOutput();
         //Dir where annotationProcessors store their output resources.
         File annotationsDir = new File(out.getGeneratedSourcesDirs().getAsPath()+"/semperSwagger");

         //File where @ExceptionHolder listed
         File handlers = new File(annotationsDir, "handlers.yml");
         //File where @ReservedExceptionResponse and @RouteSince listed
         File additions = new File(annotationsDir, "additions.yml");


         //Reading handlers.yml
         HandlersRead read = UpdaterUtils.readReservedExceptions(handlers, getProject(), getLogger(), out);
         if (read != null) {
            String mainFileName = read.mainFileName(); //Main file name
            Directory swaggerDir = getProject().getLayout().getBuildDirectory().get() //Dir where micronaut places swagger output
                                         .dir("classes")
                                         .dir("java")
                                         .dir("main")
                                         .dir("META-INF")
                                         .dir("swagger");
            Set<File> files = swaggerDir
                                    .getAsFileTree()
                                    .matching((file) -> {
                                       file.include(mainFileName + ".yml", mainFileName + ".json");
                                    })
                                    .getFiles(); //Searching swagger file in swagger output dir (might be json or yml)
            if (files.isEmpty()) { //No file found
               getLogger().warn("No swagger files found in {}", swaggerDir);
               return;
            }
            /*
            Iterate through files.
            There must be only one file... But who knows? Let's proceed all of them
             */
            for (File file : files) {
               String name = file.getName(); //Reading filename
               ISwaggerUpdater updater;
               if (name.endsWith(".yml")) { //If it is .yml file
                  updater = new YamlSwaggerUpdater(this);
               } else if (name.endsWith(".json")) { //If it is .json file
                  updater = new JsonSwaggerUpdater(this);
               } else { //Just for using updater without `updater = null;`. Really...
                  getLogger().warn("Unknown swagger file extension: {}", name);
                  continue;
               }
               //Process current file
               updater.process(additions, file, read.examples());
            }
         }

      }
      catch (IOException e) { //Some type of IOError with files...
         throw new RuntimeException(e);
      }
   }

}
