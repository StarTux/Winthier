package com.winthier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandHandler {
        String name() default "";
        String description() default "";
        String[] aliases() default {};
        String permission() default "";
        String permissionDefault() default "";
        String permissionMessage() default "";
        String usage() default "";
}
