package xyz.oribuin.skyhoppers.obj;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Container;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SkyHopper {

    private boolean enabled;
    @Nullable
    private Location location;
    @Nullable
    private Container linked;
    @NotNull
    private FilterType filterType;
    @NotNull
    private List<Material> filterItems;
    @Nullable
    private UUID owner;

    public SkyHopper(Location location) {
        this.location = location;
        this.enabled = true;
        this.linked = null;
        this.filterType = FilterType.BLACKLIST;
        this.filterItems = new ArrayList<>();
        this.owner = null;
    }

    public SkyHopper() {
        this.enabled = true;
        this.location = null;
        this.linked = null;
        this.filterType = FilterType.BLACKLIST;
        this.filterItems = new ArrayList<>();
        this.owner = null;
    }

    public static List<Material> validContainers() {
        List<Material> materials = new ArrayList<>(Arrays.asList(
                Material.BARREL,
                Material.BLAST_FURNACE,
                Material.CHEST,
                Material.TRAPPED_CHEST,
                Material.DISPENSER,
                Material.DROPPER,
                Material.FURNACE,
                Material.HOPPER,
                Material.SMOKER
        ));

        materials.addAll(Tag.SHULKER_BOXES.getValues());
        return materials;
    }

    @Nullable
    public Location getLocation() {
        return location;
    }

    public void setLocation(@Nullable Location location) {
        this.location = location;
    }

    @Nullable
    public Container getLinked() {
        return linked;
    }

    public void setLinked(@Nullable Container linked) {
        this.linked = linked;
    }

    @NotNull
    public FilterType getFilterType() {
        return filterType;
    }

    public void setFilterType(@NotNull FilterType filterType) {
        this.filterType = filterType;
    }

    public @NotNull List<Material> getFilterItems() {
        return filterItems;
    }

    public void setFilterItems(@NotNull List<Material> filterItems) {
        this.filterItems = filterItems;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Nullable
    public UUID getOwner() {
        return owner;
    }

    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;
    }
}
