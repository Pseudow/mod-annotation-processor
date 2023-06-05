package net.alfheim.tool.map.api.fabric;

import net.alfheim.tool.map.api.Side;

/**
 * This annotation describes an entry that will be communicated with Fabric loader.
 * <p>
 * Those entries are going to be executed by Fabric loader itself.
 */
public @interface Entrypoint {
    String path();

    Side side();
}
