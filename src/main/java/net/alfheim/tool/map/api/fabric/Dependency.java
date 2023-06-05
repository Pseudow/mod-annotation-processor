package net.alfheim.tool.map.api.fabric;

/**
 * This annotation represents a Fabric mod dependency.
 */
public @interface Dependency {
    String id();

    String version();

    DependencyRelationType relation();
}
