<h1>Micronaut Swagger Additions</h1>

I created this project for myself and I'd like to share it with others.
## Simple Description
This project add couple new annotations that you can use in your micronaut project to improve your Swagger Doc. Almost all of them must be set in top of Routing controller methods (@Get, @Post, @...)

* [@Pinned](processor/src/main/java/ru/semperante/swagger/processor/Pinned.java) - Mark route as "Pinned" - adding Tags "Pinned" that will be at top of all tags
* [@RouteSince](processor/src/main/java/ru/semperante/swagger/processor/RouteSince.java) - Set route "creation" date. You can specify date in ISO format and number of days that this route will be considered "new" adding tag NEW to it. Since micronaut generates Swagger at compile time, I can only check this as compile time, so NO in runtime tag will not be removed after N days
* [@ExceptionHolder](processor/src/main/java/ru/semperante/swagger/processor/ExceptionHolder.java) - Mark class (type) as Reserved exceptions holder. Meaning this class contains `public static final` fields of type `HttpStatusException` (or derived classes) that used as Http exception in your services.
* [@ReservedExceptionResponse](processor/src/main/java/ru/semperante/swagger/processor/ReservedExceptionResponse.java) - Used to list all reserved exception names that this routing can return to user. You need to specify array of field names that present in one of classes marked with  [@ExceptionHolder](processor/src/main/java/ru/semperante/swagger/processor/ExceptionHolder.java)

## Usage
Add this to your `build.gradle`:
```groovy

dependencies {
    //...
    annotationProcessor("io.github.semperetante:micronaut-swagger-library:1.0")
    compileOnly("io.github.semperetante:micronaut-swagger-library:1.0")
    aotPlugins("io.github.semperetante:micronaut-swagger-aot-plugin:1.0")
    //...
}



micronaut {
   //...
    aot {
        //...
        configurationProperties.put("ru.semperante.swagger.enabled", "true")
    }
}
```
And all annotations will be proceeded at **!!!AOT!!!** tasks. That means only jars created as a result of AOT tasks will contain modified swagger. 

I can't inject this in normal build-time, because that needs `gradle-plugin` not `aotPlugin` which is not target in my use-case. And yes @Pinned and @RouteSince can be injected in annotationProcessor, but again, it's more useful as it is.