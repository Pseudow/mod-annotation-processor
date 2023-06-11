package net.alfheim.tool.map.api.fabric;

import net.alfheim.tool.map.api.Side;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is a bridge to Fabric json configuration file.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface FabricMod {
    SchemaVersion schemaVersion() default SchemaVersion.V1;

    String id();

    String name();

    /**
     * Please refer to <a href="https://semver.org/">https://semver.org/</a> for
     * versioning specifications
     */
    String version();

    Side side();

    Entrypoint[] entryPoints() default {};

    String environment() default "*";

    Dependency[] dependencyRelations() default {};

    String description() default "This is an example description!";

    String[] authors() default {};

    String license() default "CC0-1.0";

    String icon() default "assets/modid/icon.png";
}
