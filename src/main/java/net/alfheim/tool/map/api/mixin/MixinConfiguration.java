package net.alfheim.tool.map.api.mixin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is a bridge to Mixin json configuration file.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface MixinConfiguration {
    boolean required() default true;

    String fileBaseName() default "";

    String minVersion() default "0.8";

    String packagePath();

    String compatibilityLevel() default "JAVA_17";

    MixinEntry[] mixinEntries() default {};
}
