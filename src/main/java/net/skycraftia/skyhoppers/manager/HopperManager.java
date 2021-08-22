package net.skycraftia.skyhoppers.manager;

import com.google.gson.Gson;
import net.skycraftia.skyhoppers.SkyHoppersPlugin;
import net.skycraftia.skyhoppers.obj.FilterItems;
import net.skycraftia.skyhoppers.obj.FilterType;
import net.skycraftia.skyhoppers.obj.SkyHopper;
import net.skycraftia.skyhoppers.util.PluginUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Hopper;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.oribuin.gui.Item;
import xyz.oribuin.orilibrary.manager.Manager;
import xyz.oribuin.orilibrary.util.HexUtils;
import xyz.oribuin.orilibrary.util.StringPlaceholders;

import java.util.*;
import java.util.stream.Collectors;

public class HopperManager extends Manager {

    private final SkyHoppersPlugin plugin = (SkyHoppersPlugin) this.getPlugin();

    // Define all the namespace keys.
    private final NamespacedKey enabled = new NamespacedKey(this.plugin, "enabled");
    private final NamespacedKey linked = new NamespacedKey(this.plugin, "linked");
    private final NamespacedKey filterType = new NamespacedKey(this.plugin, "filtertype");
    private final NamespacedKey filterItems = new NamespacedKey(this.plugin, "filteritems");

    private final Gson gson = new Gson();


    public HopperManager(SkyHoppersPlugin plugin) {
        super(plugin);
    }

    /**
     * Save a hopper's values most recent linked container.
     *
     * @param hopper The hopper to be saved.
     */
    public void saveHopper(SkyHopper hopper) {

        if (hopper.getLocation() == null)
            return;

        final Block hopperBlock = hopper.getLocation().getBlock();
        if (!(hopperBlock.getState() instanceof Hopper container))
            return;

        final PersistentDataContainer pdc = container.getPersistentDataContainer();

        // Why the hell spigot makes me have to update the container each change instead of doing it the way ItemMeta does it
        // I will never understand but I digress.
        pdc.set(enabled, PersistentDataType.STRING, String.valueOf(hopper.isEnabled()));
        container.update();
        pdc.set(filterType, PersistentDataType.STRING, hopper.getFilterType().name());
        container.update();

        if (hopper.getFilterItems().size() > 0) {
            pdc.set(filterItems, PersistentDataType.STRING, serializeMaterials(hopper.getFilterItems()));
            container.update();
        }

        if (hopper.getLinked() != null) {
            pdc.set(linked, PersistentDataType.STRING, serializeLocation(hopper.getLinked().getLocation()));
            container.update();
        }

        final DataManager data = this.plugin.getManager(DataManager.class);
        data.getCachedHoppers().put(PluginUtils.getBlockLoc(hopper.getLocation()), hopper);

        final List<UUID> hopperViewers = this.plugin.getHopperViewers().entrySet().stream()
                .filter(entry -> entry.getValue().getLocation() != null)
                .filter(entry -> PluginUtils.getBlockLoc(entry.getValue().getLocation()).equals(PluginUtils.getBlockLoc(hopper.getLocation())))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        hopperViewers.forEach(uuid -> this.plugin.getHopperViewers().put(uuid, hopper));
    }

    /**
     * Get the Custom Hopper from a block.
     *
     * @param block The hopper tile entity
     * @return An optional custom hopper.
     */
    public Optional<SkyHopper> getHopperFromBlock(Hopper block) {

        final PersistentDataContainer container = block.getPersistentDataContainer();


        // Check if the tile entity is a custom hopper
        if (!container.has(enabled, PersistentDataType.STRING)) //  || !data.getCachedHoppers().containsKey(block.getLocation())
            return Optional.empty();

        final SkyHopper skyHopper = new SkyHopper();
        skyHopper.setLocation(block.getLocation());
        skyHopper.setEnabled(Boolean.parseBoolean(container.get(enabled, PersistentDataType.STRING)));
        skyHopper.setFilterType(FilterType.valueOf(container.getOrDefault(filterType, PersistentDataType.STRING, "WHITELIST")));

        if (container.get(filterItems, PersistentDataType.STRING) != null) {
            skyHopper.setFilterItems(this.deserializeMaterials(container.get(filterItems, PersistentDataType.STRING)));
        }

        final String serializedLocation = container.get(linked, PersistentDataType.STRING);
        if (serializedLocation == null) {
            skyHopper.setLinked(null);
        } else {

            // No, I don't like this code either.
            Block linkedBlock = this.deserializeLocation(serializedLocation).getBlock();
            if (!(linkedBlock.getState() instanceof Container linkedContainer))
                skyHopper.setLinked(null);
            else
                skyHopper.setLinked(linkedContainer);

        }

        return Optional.of(skyHopper);
    }

    /**
     * Get a custom hopper from an ItemStack
     *
     * @param item The Hopper ItemStack
     * @return An optional custom hopper.
     */
    public Optional<SkyHopper> getHopperFromItem(ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return Optional.empty();

        final PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(enabled, PersistentDataType.STRING))
            return Optional.empty();

        final SkyHopper hopper = new SkyHopper();
        hopper.setEnabled(Boolean.parseBoolean(container.get(enabled, PersistentDataType.STRING)));
        hopper.setFilterType(FilterType.valueOf(container.getOrDefault(filterType, PersistentDataType.STRING, "WHITELIST")));

        if (container.get(filterItems, PersistentDataType.STRING) != null) {
            hopper.setFilterItems(this.deserializeMaterials(container.get(filterItems, PersistentDataType.STRING)));
        }

        final String serializedLocation = container.get(linked, PersistentDataType.STRING);
        if (serializedLocation == null) {
            hopper.setLinked(null);
        } else {

            // No, I don't like this code either.
            Block linkedBlock = this.deserializeLocation(serializedLocation).getBlock();
            if (!(linkedBlock.getState() instanceof Container linkedContainer))
                hopper.setLinked(null);
            else
                hopper.setLinked(linkedContainer);

        }

        return Optional.of(hopper);
    }

    /**
     * Get the Custom Hopper from a location.
     *
     * @param location The location of the hopper
     * @return An optional custom hopper.
     */
    public Optional<SkyHopper> getHopperFromLocation(Location location) {
        if (!(location.getBlock().getState() instanceof Hopper hopper))
            return Optional.empty();

        return this.getHopperFromBlock(hopper);
    }

    /**
     * Get an hopper as the ItemStack Form.
     *
     * @param hopper The Hopper
     * @return The Hopper as an ItemStack.
     */
    public ItemStack getHopperAsItem(SkyHopper hopper, int amount) {

        // Define the Item's Values
        final ItemStack item = new Item.Builder(Material.HOPPER)
                .setName(this.format(hopper, plugin.getConfig().getString("hopper.name")))
                .setLore(this.format(hopper, plugin.getConfig().getStringList("hopper.lore")))
                .setFlags(ItemFlag.HIDE_ATTRIBUTES)
                .glow(plugin.getConfig().getBoolean("hopper.glow"))
                .setAmount(amount)
                .create();

        final ItemMeta meta = item.getItemMeta();
        assert meta != null;

        // Set the Item's values.
        final PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(enabled, PersistentDataType.STRING, String.valueOf(hopper.isEnabled()));
        container.set(filterType, PersistentDataType.STRING, hopper.getFilterType().name());

        if (hopper.getFilterItems().size() > 0) {
            final String serializedMaterials = serializeMaterials(hopper.getFilterItems());
            container.set(filterItems, PersistentDataType.STRING, serializedMaterials);
        }

        if (hopper.getLinked() != null)
            container.set(linked, PersistentDataType.STRING, serializeLocation(hopper.getLinked().getLocation()));

        item.setItemMeta(meta);

        return item;
    }

    /**
     * Get the placeholders for the hopper itself,
     *
     * @param skyHopper The hopper
     * @return The placeholders for the hopper.
     */
    public StringPlaceholders getPlaceholders(SkyHopper skyHopper) {
        return StringPlaceholders.builder()
                .addPlaceholder("enabled", skyHopper.isEnabled() ? "Yes" : "No")
                .addPlaceholder("linked", PluginUtils.formatContainerLoc(skyHopper.getLinked()))
                .addPlaceholder("filterType", WordUtils.capitalizeFully(skyHopper.getFilterType().name().toLowerCase()))
                .build();
    }

    /**
     * Format a message relating to hoppers with placeholders.
     *
     * @param skyHopper The hopper
     * @param message   The message
     * @return A colorified message with hopper placeholer support.
     */
    private String format(SkyHopper skyHopper, String message) {
        return HexUtils.colorify(this.getPlaceholders(skyHopper).apply(message));
    }

    /**
     * Format a message list relating to hoppers with placeholders.
     *
     * @param skyHopper The hopper
     * @param message   The message list
     * @return A colorified message list with hopper placeholer support.
     */
    private List<String> format(SkyHopper skyHopper, List<String> message) {
        return message.stream().map(s -> format(skyHopper, s)).collect(Collectors.toList());
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

        final YamlConfiguration config = new YamlConfiguration();
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

        final YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(new String(Base64.getDecoder().decode(serialized)));
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        Location loc = config.getLocation("location");
        if (loc == null || loc.getWorld() == null)
            return null;

        return loc;
    }

    /**
     * Serialize a location into a base64 value.
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

        return gson.fromJson(serialized, FilterItems.class).getItems()
                .stream()
                .map(Material::matchMaterial)
                .collect(Collectors.toList());
    }

}
