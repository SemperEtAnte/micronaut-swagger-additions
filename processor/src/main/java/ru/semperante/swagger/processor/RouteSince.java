package ru.semperante.swagger.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that used to mark when route was created.
 * If route created after (now-daysToShow), then it will be marked with tag "NEW" inside of swagger
 * <p>
 * Used with micronaut (@Get, @Post, etc.) annotations
 * </p>
 *
 * Example:
 * <pre>
 * {@code
 *    @Post("auth")
 *    @RouteSince("2025-01-02")
 *    @Secured(SecurityRule.IS_ANONYMOUS)
 *    public AuthResponse<User> auth(@Valid @Body AuthRequests.LoginUser req) {
 *       return userService.authUser(req);
 *    }
 * }
 * </pre>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface RouteSince {
   /**
    * @return date in ISO format (2024-12-31)
    */
   String value();

   /**
    * @return For how many days show this route as NEW
    */
   int daysToShow() default 7;
}
