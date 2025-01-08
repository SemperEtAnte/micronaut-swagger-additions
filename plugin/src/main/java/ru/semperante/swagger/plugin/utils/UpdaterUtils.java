package ru.semperante.swagger.plugin.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import io.micronaut.http.exceptions.HttpStatusException;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.SourceSetOutput;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import ru.semperante.swagger.models.AdditionsConfigModel;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UpdaterUtils {

   public static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
   public static final ObjectMapper JSON_MAPPER = new ObjectMapper(new JsonFactory());
   private static final String API_ERROR_EXAMPLE =
         """
         {\
           "status": "%s",\
           "message": "%s",\
           "errors": []\
         }""";


   @Nullable
   public static HandlersRead readReservedExceptions(File handler, Project project, Logger logger, SourceSetOutput out) throws IOException {
      if (handler.exists()) {
         Map<String, TempExceptionHolder> examples = new HashMap<>();
         AdditionsConfigModel hl = YAML_MAPPER.readValue(handler, AdditionsConfigModel.class);
         //Dir where compileJava stores .class files
         if (out == null) {
            logger.error("Cannot find project output classes dir");
            return null;
         }
         FileCollection classes = out.getClassesDirs();
         Set<File> compiledClassesDirs = classes.getFiles();
         URL[] urls = new URL[compiledClassesDirs.size()];
         int i = 0;
         for (File file : compiledClassesDirs) {
            urls[i++] = file.toURI().toURL();
         }
         logger.warn("Classes: {}", compiledClassesDirs);

         //Creating ClassLoader that will read output of compileJava, to find classes marked as ReservedException holders
         try (URLClassLoader cl = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader())) {
            for (String s : hl.handlers()) { //Iterating through handlers list
               try {
                  Class<?> clazz = cl.loadClass(s); //Loading class from generated .yml
                  for (Field f : clazz.getDeclaredFields()) { //Iterating through this class fields
                     try {
                        Object obj = f.get(null); //Getting field for our uses (only if it is non-static)
                        if (obj instanceof HttpStatusException hse) { //If it is valid http exception

                           //Adding field to examples.
                           //If non-null value returned - we've got duplicate exception field
                           //The last "duplicate" will be used
                           if (examples.put(f.getName(), new TempExceptionHolder(hse.getStatus(),
                                 API_ERROR_EXAMPLE.formatted(hse.getStatus().getCode() + " " + hse.getStatus().getReason(), hse.getMessage()))) != null) {
                              logger.warn("Found duplicate reserved exception field {}", f.getName());
                           }
                        }
                     }
                     catch (IllegalAccessException e) { //Can't access field inside class
                        logger.error("Field {} cannot be taken in {}. Maybe private or non-static", s, clazz);
                     }
                  }
               }
               catch (ClassNotFoundException e) {//ClassLoader cannot find class (mb something with compileJava?)
                  logger.error("Class {} is not found", s);
               }
            }
         }

         //Reading title and version props
         //If no @OpenApi
         //Making swagger config file as result:
         //(lower-case `title` with replaced spaces with "-") + "-" (lower-case `version` with replaced spaces with "-")
         //Example:
         //    Title: "My project   swagger"
         //    version: "1.12.f32"
         //Result: "my-project---swagger-1.12.f32"
         String title = hl.title();
         if (title == null) {
            title = project.getName();
         }
         String version = hl.version();
         if (version == null) {
            version = project.getVersion().toString();
         }

         return new HandlersRead((title + "-" + version).toLowerCase().replaceAll(" ", "-"), examples);
      } else {
         logger.error("File handlers.yml does not exists. Maybe annotation processor wasn't called");
         return null;
      }
   }
}
