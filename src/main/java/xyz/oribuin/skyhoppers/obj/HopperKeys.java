package xyz.oribuin.skyhoppers.obj;

import org.bukkit.NamespacedKey;

public enum HopperKeys {
    ENABLED, LINKED, FILTER_TYPE, FILTER_ITEMS, OWNER;

    private NamespacedKey key;

    HopperKeys() {
        this.key = new NamespacedKey("skyhoppers", this.name().toLowerCase());
    }

    public NamespacedKey getKey() {
        return key;
    }

    public void setKey(NamespacedKey key) {
        this.key = key;
    }

}
