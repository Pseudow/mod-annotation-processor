package net.alfheim.tool.map.api.fabric;

/**
 * This enum defines the different relations the current mod has with other dependencies.
 */
public enum DependencyRelationType {
    /**
     * For dependencies required to run. Without them, a game will crash.
     */
    DEPENDS,
    /**
     * For dependencies not required to run. Without them, a game will log a warning.
     */
    RECOMMENDS,
    /**
     *  For dependencies not required to run. Use this as a kind of metadata.
     */
    SUGGESTS,
    /**
     * For mods whose together with yours might cause a game crash. With them, a game will crash.
     */
    BREAKS,
    /**
     * For mods whose together with yours cause some kind of bugs, etc. With them, a game will log a warning.
     */
    CONFLICTS
}
