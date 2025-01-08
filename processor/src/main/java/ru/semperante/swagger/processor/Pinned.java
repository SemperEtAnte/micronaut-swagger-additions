package ru.semperante.swagger.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to pin routes in swagger doc
 * <p>
 * Example:
 * <pre>
 * {@code
 *    @Post("auth")
 *    @Pinned
 *    @Secured(SecurityRule.IS_ANONYMOUS)
 *    public AuthResponse<User> auth(@Valid @Body AuthRequests.LoginUser req) {
 *       return userService.authUser(req);
 *    }
 * }
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Pinned {
}
