package net.skycraftia.skyhoppers.obj;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CustomHopper {

    private boolean enabled;
    @Nullable
    private Location location;
    @Nullable
    private Container linked;
    @NotNull
    private FilterType filterType;
    @NotNull
    private List<Material> filterItems;

    public CustomHopper() {
        this.location = null;
        this.enabled = true;
        this.linked = null;
        this.filterType = FilterType.BLACKLIST;
        this.filterItems = Collections.singletonList(Material.GRASS_BLOCK);
    }

    public static List<Material> validContainers() {
        return Arrays.asList(
                Material.BARREL,
                Material.BLAST_FURNACE,
                Material.BREWING_STAND,
                Material.CHEST,
                Material.DISPENSER,
                Material.DROPPER,
                Material.FURNACE,
                Material.HOPPER,
                Material.SHULKER_BOX,
                Material.SMOKER
        );
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

}
