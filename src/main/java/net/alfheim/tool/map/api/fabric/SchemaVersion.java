package net.alfheim.tool.map.api.fabric;

/**
 * The schema version is a value needed by Fabric, it should always be 1.
 */
public enum SchemaVersion {
    V1(1);

    private final int value;

    SchemaVersion(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
