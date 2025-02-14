package ru.semperante.swagger.plugin.updater;

import ru.semperante.swagger.plugin.utils.TempExceptionHolder;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface ISwaggerUpdater {
   /**
    * Processes swagger api doc file
    *
    * @param additionsFile file generated by annotationProcessor module
    * @param mainFile      file generated by micronaut openapi-swagger (build/classes/META-INF/swagger/{name}.yml (or .json)
    * @param examples      mapped examples for reserved exceptions fields
    */
   void process(File additionsFile, File mainFile, Map<String, TempExceptionHolder> examples) throws IOException;
}
