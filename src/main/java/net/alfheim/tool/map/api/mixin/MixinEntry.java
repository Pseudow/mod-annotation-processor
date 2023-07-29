package net.alfheim.tool.map.api.mixin;

import net.alfheim.tool.map.api.Side;

public @interface MixinEntry {
    String className();

    Side side() default Side.BOTH;
}
