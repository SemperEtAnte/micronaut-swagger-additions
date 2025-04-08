package ru.semperante.updater;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.semperante.utils.UpdaterUtils;

/**
 * Updater for yaml format of swagger
 */
public class YamlSwaggerUpdater extends AbstractSwaggerUpdater {


   @Override
   protected ObjectMapper getMapper() {
      return UpdaterUtils.YAML_MAPPER;
   }
}
