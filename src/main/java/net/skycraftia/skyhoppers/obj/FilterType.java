package net.skycraftia.skyhoppers.obj;

public enum FilterType {
    WHITELIST("Only specific items are allowed in."),
    BLACKLIST("Deny specific items from going in."),
    DESTROY("Destroy specific items the hopper picks up.");

    private final String desc;

    FilterType(final String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

}
