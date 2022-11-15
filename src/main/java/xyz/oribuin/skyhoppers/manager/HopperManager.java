package xyz.oribuin.skyhoppers.manager;

import com.google.gson.Gson;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Hopper;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.skyhoppers.hopper.HopperKeys;
import xyz.oribuin.skyhoppers.hopper.SkyHopper;
import xyz.oribuin.skyhoppers.hopper.filter.FilterItems;
import xyz.oribuin.skyhoppers.hopper.filter.FilterType;
import xyz.oribuin.skyhoppers.manager.ConfigurationManager.Settings;
import xyz.oribuin.skyhoppers.util.ItemBuilder;
import xyz.oribuin.skyhoppers.util.PluginUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class HopperManager extends Manager {

    private final Map<Location, SkyHopper> hoppers = new HashMap<>();
    private final Map<UUID, SkyHopper> hopperViewers = new HashMap<>();
    private final Gson gson = new Gson();

    private final DataManager dataManager = this.rosePlugin.getManager(DataManager.class);

    public HopperManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        this.hoppers.clear();
        this.hopperViewers.clear();

        this.dataManager.loadHoppers();
        this.rosePlugin.getServer().getScheduler().runTask(this.rosePlugin, this::updateHoppers);
    }

    public void updateHoppers() {
        this.hoppers.clear();
        this.dataManager.getHopperLocations().forEach(location -> this.hoppers.put(location, new SkyHopper(location)));
    }

    /**
     * Create a brand-new hopper
     *
     * @param block The block to create the hopper at
     */
    public void createHopper(Block block, Player owner) {
        final SkyHopper skyHopper = new SkyHopper(block.getLocation());

        skyHopper.setOwner(owner.getUniqueId());
        this.dataManager.addHopper(block.getLocation());
        this.saveHopperBlock(skyHopper);

    }

    /**
     * Remove a hopper
     *
     * @param skyHopper The hopper to remove
     */
    public void removeHopper(SkyHopper skyHopper) {
        this.dataManager.removeHopper(skyHopper.getLocation());
        this.hoppers.remove(skyHopper.getLocation());

        skyHopper.setLocation(null);
    }

    /**
     * Save a hopper container to the config
     *
     * @param skyHopper The hopper to save
     */
    public void saveHopperBlock(SkyHopper skyHopper) {

        if (skyHopper.getLocation() == null)
            return;

        final var hopperBlock = skyHopper.getLocation().getBlock();
        var hopperState = (org.bukkit.block.Hopper) hopperBlock.getState();

        // I hate having to update every single time but I can't think of a better way to do this.
        final var container = hopperState.getPersistentDataContainer();

        container.set(HopperKeys.ENABLED.getKey(), PersistentDataType.INTEGER, skyHopper.isEnabled() ? 1 : 0);
        hopperState.update();

        // Set the hopper owner
        if (skyHopper.getOwner() != null) {
            container.set(HopperKeys.OWNER.getKey(), PersistentDataType.STRING, skyHopper.getOwner().toString());
            hopperState.update();
        }

        // Set the filter items
        if (!skyHopper.getFilterItems().isEmpty()) {
            container.set(HopperKeys.FILTER_ITEMS.getKey(), PersistentDataType.STRING, this.serializeMaterials(skyHopper.getFilterItems()));
            hopperState.update();
        }

        // Set the filter type
        container.set(HopperKeys.FILTER_TYPE.getKey(), PersistentDataType.STRING, skyHopper.getFilterType().name());
        hopperState.update();

        // Set the linked hopper
        if (skyHopper.getLinked() != null) {
            container.set(HopperKeys.LINKED.getKey(), PersistentDataType.STRING, serializeLocation(skyHopper.getLinked().getLocation()));
        } else {
            container.remove(HopperKeys.LINKED.getKey());
        }

        hopperState.update();
        this.hoppers.put(PluginUtils.getBlockLoc(skyHopper.getLocation()), skyHopper);
    }

    /**
     * Save a hopper into an itemstack
     *
     * @param itemStack The itemstack
     * @param skyHopper The hopper
     * @return The new itemstack with the hopper data
     */
    public ItemStack saveHopperItem(ItemStack itemStack, SkyHopper skyHopper) {
        final var meta = itemStack.getItemMeta();
        if (meta == null)
            return itemStack;

        // I hate having to update every single time but I can't think of a better way to do this.
        final var container = meta.getPersistentDataContainer();

        container.set(HopperKeys.ENABLED.getKey(), PersistentDataType.INTEGER, skyHopper.isEnabled() ? 1 : 0);

        // Set the hopper owner
        if (skyHopper.getOwner() != null) {
            container.set(HopperKeys.OWNER.getKey(), PersistentDataType.STRING, skyHopper.getOwner().toString());
        }

        // Set the filter items
        if (!skyHopper.getFilterItems().isEmpty()) {
            container.set(HopperKeys.FILTER_ITEMS.getKey(), PersistentDataType.STRING, this.serializeMaterials(skyHopper.getFilterItems()));
        }

        // Set the filter type
        container.set(HopperKeys.FILTER_TYPE.getKey(), PersistentDataType.STRING, skyHopper.getFilterType().name());

        // Set the linked hopper
        if (skyHopper.getLinked() != null) {
            container.set(HopperKeys.LINKED.getKey(), PersistentDataType.STRING, serializeLocation(skyHopper.getLinked().getLocation()));
        } else {
            container.remove(HopperKeys.LINKED.getKey());
        }

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * Get a hopper from a location
     *
     * @param location The location of the hopper
     * @return The hopper object
     */
    @Nullable
    public SkyHopper getHopper(Location location) {
        return this.getHopper(location.getBlock());
    }

    @Nullable
    public SkyHopper getHopperFromCache(@Nullable Location location) {
        if (location == null)
            return null;

        return this.hoppers.get(PluginUtils.getBlockLoc(location));
    }

    /**
     * Get a hopper from a block
     *
     * @param block The block of the hopper
     * @return The hopper object
     */
    @Nullable
    public SkyHopper getHopper(Block block) {
        // Check if the block is a hopper
        if (!(block.getState() instanceof org.bukkit.block.Hopper hopperState))
            return null;

        return this.getHopperFromContainer(hopperState.getPersistentDataContainer(), block.getLocation());
    }

    @Nullable
    public SkyHopper getHopper(Hopper hopper) {
        return this.getHopperFromContainer(hopper.getPersistentDataContainer(), hopper.getLocation());
    }

    /**
     * Get a hopper from an itemstack
     *
     * @param itemStack The itemstack to get the hopper from
     * @return The hopper object
     */
    public SkyHopper getHopper(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != Material.HOPPER)
            return null;

        final var container = itemStack.getItemMeta().getPersistentDataContainer();
        return this.getHopperFromContainer(container, null);
    }

    /**
     * Get a hopper from a container
     *
     * @param container The container of the hopper
     * @param location  The location of the hopper
     * @return The hopper object
     */
    public SkyHopper getHopperFromContainer(@NotNull PersistentDataContainer container, @Nullable Location location) {
        var enabled = container.get(HopperKeys.ENABLED.getKey(), PersistentDataType.INTEGER);
        if (enabled == null)
            return null;

        // Create the hopper object
        var skyHopper = new SkyHopper(location);

        // Set the enabled state
        skyHopper.setEnabled(enabled == 1);

        // Set the filter type
        skyHopper.setFilterType(FilterType.valueOf(container.get(HopperKeys.FILTER_TYPE.getKey(), PersistentDataType.STRING)));

        // Set the filter items
        if (container.has(HopperKeys.FILTER_ITEMS.getKey()))
            skyHopper.setFilterItems(this.deserializeMaterials(container.get(HopperKeys.FILTER_ITEMS.getKey(), PersistentDataType.STRING)));

        // Set the owner
        if (container.has(HopperKeys.OWNER.getKey()))
            skyHopper.setOwner(UUID.fromString(container.get(HopperKeys.OWNER.getKey(), PersistentDataType.STRING)));

        // Set the linked hopper
        final String serializedLocation = container.get(HopperKeys.LINKED.getKey(), PersistentDataType.STRING);
        if (serializedLocation == null) {
            skyHopper.setLinked(null);
        } else {
            var linkedBlock = this.deserializeLocation(serializedLocation).getBlock();
            if (!(linkedBlock.getState() instanceof org.bukkit.block.Container linkedHopperState))
                skyHopper.setLinked(null);
            else
                skyHopper.setLinked(linkedHopperState);
        }

        this.saveHopperBlock(skyHopper);
        return skyHopper;
    }

    /**
     * Give a player a hopper as an item
     *
     * @param skyHopper The hopper to give
     * @param amount    The amount of hoppers to give
     * @return The itemstack of the hopper
     */
    public ItemStack getHopperAsItem(SkyHopper skyHopper, int amount) {

        var placeholders = this.getPlaceholders(skyHopper);

        final var itemStack = new ItemBuilder(Material.HOPPER)
                .setName(PluginUtils.format(null, Settings.HOPPER_ITEM_NAME.getString(), placeholders))
                .setLore(PluginUtils.format(null, Settings.HOPPER_ITEM_LORE.getStringList(), placeholders))
                .glow(Settings.HOPPER_ITEM_GLOW.getBoolean())
                .setAmount(amount)
                .create();

        return this.saveHopperItem(itemStack, skyHopper);
    }

    /**
     * Get all hoppers with a linked container
     *
     * @return A list of hoppers
     */
    public List<SkyHopper> getLinkedHoppers() {
        List<SkyHopper> linkedHoppers = new ArrayList<>();
        for (var skyHopper : this.hoppers.values()) {
            if (skyHopper.getLinked() != null)
                linkedHoppers.add(skyHopper);
        }

        return linkedHoppers;
    }

    /**
     * Get a hopper from a container if linked
     *
     * @param container The container to get the hopper from
     * @return The hopper object
     */
    public SkyHopper getHopperFromContainer(Container container) {
        for (var skyHopper : this.getLinkedHoppers()) {
            Container linked = skyHopper.getLinked(); // it should never be null but intelli doesn't know that
            if (linked != null && linked.getLocation().equals(container.getLocation()))
                return skyHopper;
        }

        return null;

    }

    /**
     * Get placeholders for a hopper object
     *
     * @param skyHopper The hopper object
     * @return The placeholders
     */
    public StringPlaceholders getPlaceholders(SkyHopper skyHopper) {
        return StringPlaceholders.builder()
                .addPlaceholder("enabled", skyHopper.isEnabled() ? "Enabled" : "Disabled")
                .addPlaceholder("filter_type", PluginUtils.formatEnum(skyHopper.getFilterType().name()))
                .addPlaceholder("owner", skyHopper.getOwner() == null ? "None" : Bukkit.getOfflinePlayer(skyHopper.getOwner()).getName())
                .addPlaceholder("linked", PluginUtils.formatContainerLoc(skyHopper.getLinked()))
                .build();
    }


    /**
     * Get a list of all hoppers
     *
     * @return A list of all hoppers
     */
    public List<SkyHopper> getHoppers() {
        return new ArrayList<>(this.hoppers.values());
    }

    /**
     * Get a list of all hoppers that are enabled
     *
     * @return A list of all enabled hoppers
     */
    public List<SkyHopper> getEnabledHoppers() {
        return this.hoppers.values().stream().filter(SkyHopper::isEnabled).collect(Collectors.toList());
    }

    /**
     * Let a player visualise a hopper
     *
     * @param player    The player who
     * @param skyHopper The hopper to add
     */
    public void addHopperViewer(Player player, SkyHopper skyHopper) {
        this.hopperViewers.put(player.getUniqueId(), skyHopper);
    }

    /**
     * Remove a player from the hopper viewer list
     *
     * @param player The player to remove
     */
    public void removeHopperViewer(Player player) {
        this.hopperViewers.remove(player.getUniqueId());
    }

    /**
     * Get the hopper a player is visualising
     *
     * @param player The player to get the hopper for
     * @return The hopper the player is visualising
     */
    public SkyHopper getHopperViewer(Player player) {
        return this.hopperViewers.get(player.getUniqueId());
    }


    /**
     * Get a list of hoppers that are being visualised by a player
     *
     * @return A list of hoppers
     */
    public List<SkyHopper> getViewedHoppers() {
        return new ArrayList<>(this.hopperViewers.values());
    }

    /**
     * Get a list of all hoppers that are owned by a player
     *
     * @param owner The owner of the hoppers
     * @return A list of all hoppers owned by the player
     */
    public List<SkyHopper> getHoppersByOwner(UUID owner) {
        return this.hoppers.values().stream()
                .filter(hopper -> hopper.getOwner() != null && hopper.getOwner().equals(owner))
                .collect(Collectors.toList());
    }

    /**
     * Get all hoppers that are owned by a player
     *
     * @param player The player to get the hoppers for
     * @return A list of hoppers
     */
    public List<SkyHopper> getHoppersByOwner(Player player) {
        return this.getHoppersByOwner(player.getUniqueId());
    }

    /**
     * Convert a list of materials into a json array to store in a string.
     *
     * @param materials The location.
     * @return The serialized location.
     */
    private String serializeMaterials(List<Material> materials) {
        return gson.toJson(new FilterItems(materials.stream().map(Material::name).collect(Collectors.toList())));
    }

    /**
     * Deserialize a Base64 Value into a Location
     *
     * @param serialized The serialized base64 value
     * @return The deserialized location.
     */
    private List<Material> deserializeMaterials(final String serialized) {
        if (serialized == null)
            return new ArrayList<>();

        return gson.fromJson(serialized, FilterItems.class).getFilterItems()
                .stream()
                .map(Material::matchMaterial)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Serialize a location into a base64 value.
     *
     * @param location The location.
     * @return The serialized location.
     */
    private String serializeLocation(Location location) {
        if (location == null)
            return null;

        final var config = new YamlConfiguration();
        config.set("location", location);
        return Base64.getEncoder().encodeToString(config.saveToString().getBytes());
    }

    /**
     * Deserialize a Base64 Value into a Location
     *
     * @param serialized The serialized base64 value
     * @return The deserialized location.
     */
    private Location deserializeLocation(final String serialized) {
        if (serialized == null)
            return null;

        final var config = new YamlConfiguration();
        try {
            config.loadFromString(new String(Base64.getDecoder().decode(serialized)));
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        var loc = config.getLocation("location");
        if (loc == null || loc.getWorld() == null)
            return null;

        return loc;
    }

    @Override
    public void disable() {

    }

    public Map<UUID, SkyHopper> getHopperViewers() {
        return hopperViewers;
    }

}
