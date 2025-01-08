package ru.semperante.swagger.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that used to list reserved exceptions on top of controller method (@Get, @Post, etc.)
 * <p>
 * List names of public static fields that will be searched inside classes marked by {@link ExceptionHolder}
 * Example controller:
 * </p>
 * <p>
 * <pre>
 * {@code
 *    @Post("auth")
 *    @ReservedExceptionResponse(value = {"INCORRECT_PASSWORD","USER_NOT_FOUND"})
 *    @Secured(SecurityRule.IS_ANONYMOUS)
 *    public AuthResponse<User> auth(@Valid @Body AuthRequests.LoginUser req) {
 *       return userService.authUser(req);
 *    }
 * }
 * </pre>
 * </p>
 * <p>
 * Example holder:
 * <pre>
 * {@code
 *
 * @ExceptionHolder
 * public final class ReservedExceptions {
 *     public static final HttpStatusException USER_NOT_FOUND = new HttpStatusException(HttpStatus.NOT_FOUND, "User not found");
 *     public static final HttpStatusException INCORRECT_PASSWORD = new HttpStatusException(HttpStatus.UNAUTHORIZED, "Incorrect password");
 * }
 * }
 * </pre>
 * </p>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface ReservedExceptionResponse {
   String[] value();
}
