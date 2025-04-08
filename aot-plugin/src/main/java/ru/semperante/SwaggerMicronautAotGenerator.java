package ru.semperante;

import io.micronaut.aot.core.AOTContext;
import io.micronaut.aot.core.AOTModule;
import io.micronaut.aot.core.codegen.AbstractCodeGenerator;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.exceptions.HttpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.semperante.swagger.models.AdditionsConfigModel;
import ru.semperante.updater.ISwaggerUpdater;
import ru.semperante.updater.JsonSwaggerUpdater;
import ru.semperante.updater.YamlSwaggerUpdater;
import ru.semperante.utils.HandlersRead;
import ru.semperante.utils.UpdaterUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.stream.Stream;

import static ru.semperante.utils.UpdaterUtils.YAML_MAPPER;

@AOTModule(id = "ru.semperante.swagger")
public class SwaggerMicronautAotGenerator extends AbstractCodeGenerator {
   private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerMicronautAotGenerator.class);

   @Override
   public void generate(@NonNull AOTContext context) {

      Path gen = Path.of("build/generated/sources/annotationProcessor/java/main/semperSwagger");
      Path swagger = Path.of("build/classes/java/main/META-INF/swagger/");

      try {
         AdditionsConfigModel hl = YAML_MAPPER.readValue(gen.resolve("handlers.yml").toFile(), AdditionsConfigModel.class);
         if (hl.isProceeded()) {
            return;
         } else {
            hl.setProceeded(true);
            YAML_MAPPER.writeValue(gen.resolve("handlers.yml").toFile(), hl);
         }
         LOGGER.debug("Path: {}", gen.toAbsolutePath());
         LOGGER.debug("Handlers: {}", hl.getHandlers());
         HandlersRead read = new HandlersRead(null, new HashMap<>());
         for (String s : hl.getHandlers()) {
            Class<?> clazz = Class.forName(s);
            for (Field f : clazz.getDeclaredFields()) {
               if (HttpStatusException.class.isAssignableFrom(f.getType())) {
                  HttpStatusException hse = (HttpStatusException) f.get(null);
                  read.examples().put(f.getName(), UpdaterUtils.formatException(hse));
               }
            }
         }

         LOGGER.debug("Examples: {}", read.examples());
         try (Stream<Path> paths = Files.walk(swagger, 1)) {
            paths
                  .forEach(path -> {
                     LOGGER.debug("Processing file {}", path.toAbsolutePath());
                     File file = path.toFile();
                     String name = file.getName(); //Reading filename
                     ISwaggerUpdater updater;
                     if (name.endsWith(".yml")) { //If it is .yml file
                        updater = new YamlSwaggerUpdater();
                     } else if (name.endsWith(".json")) { //If it is .json file
                        updater = new JsonSwaggerUpdater();
                     } else { //Just for using updater without `updater = null;`. Really...
                        return;
                     }
                     //Process current file
                     try {
                        updater.process(gen.resolve("additions.yml").toFile(), file, read.examples());
                     }
                     catch (IOException e) {
                        throw new RuntimeException(e);
                     }
                  });
         }

      }
      catch (IOException | ClassNotFoundException | IllegalAccessException e) {
         throw new RuntimeException(e);
      }
   }
}
