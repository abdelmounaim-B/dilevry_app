package org.tpjava.Gateway.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionRequired {
    String value(); // The required permission for the annotated endpoint
}