package ru.semperante.swagger.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that used to mark class that contains reserved exceptions as a public static (final) field
 * example:
 * <pre>
 * {@code @ExceptionHolder
 * public class ReservedExceptions(){
 *    public static final HttpStatusException USER_NOT_FOUND = new HttpStatusException(HttpStatus.NOT_FOUND, "User not found");
 *    public static final HttpStatusException FILE_NOT_FOUND = new HttpStatusException(HttpStatus.NOT_FOUND, "File not found");
 *    public static final HttpStatusException DB_ERROR = new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error");
 *    public static final HttpStatusException IO_ERROR = new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "IO error");
 *    public static final HttpStatusException ACCESS_DENIED = new HttpStatusException(HttpStatus.FORBIDDEN, "Access denied");
 * }}
 *
 * </pre>
 *
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface ExceptionHolder {
}
