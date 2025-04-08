package ru.semperante.updater;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.semperante.utils.UpdaterUtils;

/**
 * Updater for .json format of swagger
 */
public class JsonSwaggerUpdater extends AbstractSwaggerUpdater {



   @Override
   protected ObjectMapper getMapper() {
      return UpdaterUtils.JSON_MAPPER;
   }
}
