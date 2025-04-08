package ru.semperante.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import io.micronaut.http.exceptions.HttpStatusException;

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


   public static TempExceptionHolder formatException(HttpStatusException hse) {
      return new TempExceptionHolder(hse.getStatus(),
            API_ERROR_EXAMPLE.formatted(hse.getStatus().getCode() + " " + hse.getStatus().getReason(), hse.getMessage()));
   }

}
