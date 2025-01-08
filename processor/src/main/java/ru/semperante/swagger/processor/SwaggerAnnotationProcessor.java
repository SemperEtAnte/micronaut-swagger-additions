package ru.semperante.swagger.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import ru.semperante.swagger.models.Additions;
import ru.semperante.swagger.models.AdditionsConfigModel;
import ru.semperante.swagger.models.SwaggerAdditionsModel;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({"io.swagger.v3.oas.annotations.OpenAPIDefinition"})
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class SwaggerAnnotationProcessor extends AbstractProcessor {

   public static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
   private static boolean called = false;
   private FileObject handlers;
   private FileObject additions;

   @Override
   public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
      if (called) {
         return true;
      } else {
         called = true;
      }
      Info info = null;
      for (Element element : roundEnv.getElementsAnnotatedWith(OpenAPIDefinition.class)) {
         OpenAPIDefinition app = element.getAnnotation(OpenAPIDefinition.class);
         if (app.info() != null) {
            info = app.info();
            break;
         }
      }
      try {
         AdditionsConfigModel hl = new AdditionsConfigModel(new HashSet<>(), info == null ? null : info.title(), info == null ? null : info.version());
         try (Writer writer = handlers.openWriter()) {
            for (Element element : roundEnv.getElementsAnnotatedWith(ExceptionHolder.class)) {
               if (element instanceof TypeElement te) {
                  hl.handlers().add(te.getQualifiedName().toString());
               }
            }
            MAPPER.writeValue(writer, hl);
         }
         Map<String, Map<String, Additions>> additionsMap = new HashMap<>();
         proceedRouteSince(additionsMap, roundEnv);
         proceedPinned(additionsMap, roundEnv);
         proceedReservedExceptions(additionsMap, roundEnv);

         SwaggerAdditionsModel swaggerAdditionsModel = new SwaggerAdditionsModel(additionsMap.values().stream().map(Map::values).flatMap(Collection::stream).collect(Collectors.toList()));
         try (Writer writer = additions.openWriter()) {
            for (Element element : roundEnv.getElementsAnnotatedWith(ExceptionHolder.class)) {
               if (element instanceof TypeElement te) {
                  hl.handlers().add(te.getQualifiedName().toString());
               }
            }
            MAPPER.writeValue(writer, swaggerAdditionsModel);
         }
      }
      catch (IOException e) {
         throw new RuntimeException(e);
      }

      return true;
   }

   @Override
   public synchronized void init(ProcessingEnvironment processingEnv) {
      super.init(processingEnv);
      if (called) {
         return;
      }
      try {
         handlers = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "semperSwagger", "handlers.yml");
         additions = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "semperSwagger", "additions.yml");
      }
      catch (IOException e) {
         throw new RuntimeException(e);
      }

   }

   private void proceedRouteSince(Map<String, Map<String, Additions>> additionsMap, RoundEnvironment roundEnv) {
      for (Element element : roundEnv.getElementsAnnotatedWith(RouteSince.class)) {
         RouteSince rt = element.getAnnotation(RouteSince.class);
         if (LocalDate.now().minusDays(rt.daysToShow()).isBefore(LocalDate.parse(rt.value()))) {
            addTagToElement(additionsMap, element, "NEW");
         }
      }
   }

   private void proceedPinned(Map<String, Map<String, Additions>> additionsMap, RoundEnvironment roundEnv) {
      for (Element element : roundEnv.getElementsAnnotatedWith(Pinned.class)) {
         addTagToElement(additionsMap, element, "PINNED");
      }
   }

   private void proceedReservedExceptions(Map<String, Map<String, Additions>> additionsMap, RoundEnvironment roundEnv) {
      for (Element element : roundEnv.getElementsAnnotatedWith(ReservedExceptionResponse.class)) {
         String[] values = element.getAnnotation(ReservedExceptionResponse.class).value();
         Controller controller = element.getEnclosingElement().getAnnotation(Controller.class);
         if (controller != null) {
            String uri = controller.value();
            Get get = element.getAnnotation(Get.class);
            Post post = element.getAnnotation(Post.class);
            Delete delete = element.getAnnotation(Delete.class);
            Patch patch = element.getAnnotation(Patch.class);
            Put put = element.getAnnotation(Put.class);
            Options options = element.getAnnotation(Options.class);
            if (get != null) {
               addException(additionsMap, uri + "/" + get.value(), "get", values);
            }
            if (post != null) {
               addException(additionsMap, uri + "/" + post.value(), "post", values);
            }
            if (delete != null) {
               addException(additionsMap, uri + "/" + delete.value(), "delete", values);
            }
            if (patch != null) {
               addException(additionsMap, uri + "/" + patch.value(), "patch", values);
            }
            if (put != null) {
               addException(additionsMap, uri + "/" + put.value(), "put", values);
            }
            if (options != null) {
               addException(additionsMap, uri + "/" + options.value(), "options", values);
            }
         }
      }
   }

   private void addTagToElement(Map<String, Map<String, Additions>> additionsMap, Element element, String tag) {
      Controller controller = element.getEnclosingElement().getAnnotation(Controller.class);
      if (controller != null) {
         String uri = controller.value();
         Get get = element.getAnnotation(Get.class);
         Post post = element.getAnnotation(Post.class);
         Delete delete = element.getAnnotation(Delete.class);
         Patch patch = element.getAnnotation(Patch.class);
         Put put = element.getAnnotation(Put.class);
         Options options = element.getAnnotation(Options.class);
         if (get != null) {
            addTag(additionsMap, tag, uri + "/" + get.value(), "get");
         }
         if (post != null) {
            addTag(additionsMap, tag, uri + "/" + post.value(), "post");
         }
         if (delete != null) {
            addTag(additionsMap, tag, uri + "/" + delete.value(), "delete");
         }
         if (patch != null) {
            addTag(additionsMap, tag, uri + "/" + patch.value(), "patch");
         }
         if (put != null) {
            addTag(additionsMap, tag, uri + "/" + put.value(), "put");
         }
         if (options != null) {
            addTag(additionsMap, tag, uri + "/" + options.value(), "options");
         }
      }
   }

   private void addTag(Map<String, Map<String, Additions>> additionsMap, String tag, String uri, String method) {
      String res = proceedUri(uri);
      additionsMap
            .computeIfAbsent(uri, k -> new HashMap<>())
            .computeIfAbsent(method, (mt) -> new Additions(res, method)).getTags().add(tag);
   }

   private void addException(Map<String, Map<String, Additions>> additionsMap, String uri, String method, String[] values) {
      String res = proceedUri(uri);
      additionsMap
            .computeIfAbsent(uri, k -> new HashMap<>())
            .computeIfAbsent(method, (mt) -> new Additions(res, method)).getExceptions().addAll(Arrays.asList(values));
   }

   private static String proceedUri(String uri) {
      String res = ("/" + uri).replaceAll("//", "/");
      return res.endsWith("/") ? res.substring(0, res.length() - 1) : res;
   }
}
