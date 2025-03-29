package it.minusone.fastbuilder.utils.eventbus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CubingEventHandler {
    int priority() default 0;
}
