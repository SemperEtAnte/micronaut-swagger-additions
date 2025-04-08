package ru.semperante.updater;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.semperante.swagger.models.Additions;
import ru.semperante.swagger.models.SwaggerAdditionsModel;
import ru.semperante.utils.TempExceptionHolder;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractSwaggerUpdater implements ISwaggerUpdater {
   private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSwaggerUpdater.class);;

   @Override
   public void process(File additionsFile, File mainFile, Map<String, TempExceptionHolder> examples) throws IOException {
      if (!mainFile.exists()) {
         LOGGER.error("Main swagger file is not found. Was expecting at: {}", mainFile.getAbsoluteFile());
         return;
      }
      if (!additionsFile.exists()) {
         LOGGER.error("No additions file found. Maybe annotation processor wasn't called. Was expecting at: {}", additionsFile.getAbsoluteFile());
         return;
      }
      ObjectMapper mapper = getMapper();
      JsonNode mn = mapper.readTree(mainFile);
      if (mn instanceof ObjectNode mainNode) {
         SwaggerAdditionsModel additionsNode = mapper.readValue(additionsFile, SwaggerAdditionsModel.class);
         JsonNode paths = mainNode.get("paths");
         Set<String> tagsToAdd = new HashSet<>();
         if (paths != null && paths.isObject()) {
            for (Additions ad : additionsNode.additions()) {
               JsonNode uri = paths.get(ad.getUri());
               if (uri != null && !uri.isNull()) {
                  JsonNode method = uri.get(ad.getMethod());
                  if (method != null && !method.isNull() && method instanceof ObjectNode methodNode) {
                     if (ad.getTags() != null && !ad.getTags().isEmpty()) {
                        JsonNode tagsNode = method.get("tags");
                        if (tagsNode == null || tagsNode.isNull()) {
                           tagsNode = methodNode.putArray("tags");
                        }
                        if (tagsNode.isArray() && tagsNode instanceof ArrayNode tags) {
                           for (String s : ad.getTags()) {
                              tags.add(s);
                              tagsToAdd.add(s);
                           }
                        }
                     }

                     if (ad.getExceptions() != null && !ad.getExceptions().isEmpty()) {
                        JsonNode responses = method.get("responses");
                        if (responses == null || responses.isNull()) {
                           responses = methodNode.putObject("responses");
                        }
                        if (responses instanceof ObjectNode responseNode) {
                           for (String nm : ad.getExceptions()) {
                              TempExceptionHolder teh = examples.get(nm);
                              if (teh != null) {
                                 JsonNode code = responseNode.get(String.valueOf(teh.status().getCode()));
                                 if (code == null || code.isNull()) {
                                    code = responseNode
                                                 .putObject(String.valueOf(teh.status().getCode()))
                                                 .put("description", teh.description());
                                 }
                                 if (code instanceof ObjectNode statusNode) {
                                    JsonNode content = statusNode.get("content");
                                    if (content == null || content.isNull()) {
                                       content = statusNode.putObject("content");
                                    }
                                    if (content instanceof ObjectNode contentNode) {
                                       contentNode.putObject(nm).put("example", teh.example());
                                    }
                                 }
                              } else {
                                 LOGGER.warn("Example not found for uri {} and field {}. Maybe no such field inside holders", ad.getUri(), nm);
                              }
                           }
                        }
                     }
                  } else {
                     LOGGER.error("Method {} for path {} not found", ad.getMethod(), ad.getUri());
                  }
               } else {
                  LOGGER.error("Path {} not found", ad.getUri());
               }
            }
         } else {
            LOGGER.error("Cannot find \"paths\" segment of main yml file. Or it's not an object (maybe spec changed?)");
         }
         if (!tagsToAdd.isEmpty()) {
            JsonNode node = mainNode.get("tags");
            if (node == null || !node.isArray()) {
               node = mainNode.putArray("tags");
            }
            if (node instanceof ArrayNode an) {

               for (JsonNode tag : an) {
                  tagsToAdd.remove(tag.get("name").asText());
               }
               for (String s : tagsToAdd) {
                  an.insertObject(0).put("name", s);
               }
            }
         }
         mapper.writeValue(mainFile, mainNode);
      } else {
         LOGGER.error("Main node not found or is not an object.");
      }
   }

   protected abstract ObjectMapper getMapper();
}
