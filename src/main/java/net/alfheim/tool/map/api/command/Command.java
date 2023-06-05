package net.alfheim.tool.map.api.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Command {
    String name();

    String[] aliases() default {};

    String[] author() default {};

    String description() default "";

    String usage() default "";

    boolean requirePlayer() default false;
}