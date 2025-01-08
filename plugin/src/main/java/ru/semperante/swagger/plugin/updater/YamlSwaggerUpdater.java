package ru.semperante.swagger.plugin.updater;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import ru.semperante.swagger.plugin.ProceedReservedExceptionsTask;

/**
 * Updater for yaml format of swagger
 */
public class YamlSwaggerUpdater extends AbstractSwaggerUpdater {

   public YamlSwaggerUpdater(ProceedReservedExceptionsTask task) {
      super(task);
   }


   @Override
   protected ObjectMapper getMapper() {
      return new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
   }
}
