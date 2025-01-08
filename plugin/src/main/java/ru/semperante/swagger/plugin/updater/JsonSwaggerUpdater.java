package ru.semperante.swagger.plugin.updater;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.semperante.swagger.plugin.ProceedReservedExceptionsTask;
import ru.semperante.swagger.plugin.utils.UpdaterUtils;

/**
 * Updater for .json format of swagger
 */
public class JsonSwaggerUpdater extends AbstractSwaggerUpdater {

   public JsonSwaggerUpdater(ProceedReservedExceptionsTask task) {
      super(task);
   }

   @Override
   protected ObjectMapper getMapper() {
      return UpdaterUtils.JSON_MAPPER;
   }
}
