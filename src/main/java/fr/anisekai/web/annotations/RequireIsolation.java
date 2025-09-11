package fr.anisekai.web.annotations;

import fr.anisekai.sanctum.AccessScope;
import fr.anisekai.sanctum.interfaces.isolation.IsolationSession;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a controller method as requiring an active {@link IsolationSession}.
 * <p>
 * Incoming requests to methods annotated with this must include the {@code x-isolation-session} header, specifying the
 * name of the targeted {@link IsolationSession}.
 * <p>
 * <b>Note:</b> This annotation does <b>not</b> create an {@link IsolationSession}.
 * For security and maintainability, sessions should <strong>not</strong> be auto-generated here. Creating a session
 * inline within a controller route is acceptable for one-time upload scenarios, but for all other cases, session
 * management (including {@link AccessScope} attribution) should be delegated to appropriate service layers with full
 * control over the session lifecycle.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireIsolation {

}
