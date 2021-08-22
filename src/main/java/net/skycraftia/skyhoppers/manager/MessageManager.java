package net.skycraftia.skyhoppers.manager;

import me.clip.placeholderapi.PlaceholderAPI;
import net.skycraftia.skyhoppers.SkyHoppersPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.oribuin.orilibrary.manager.Manager;
import xyz.oribuin.orilibrary.util.FileUtils;
import xyz.oribuin.orilibrary.util.StringPlaceholders;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static xyz.oribuin.orilibrary.util.HexUtils.colorify;

public class MessageManager extends Manager {

    private final SkyHoppersPlugin plugin = (SkyHoppersPlugin) this.getPlugin();

    private FileConfiguration config;

    public MessageManager(SkyHoppersPlugin plugin) {
        super(plugin);
    }

    public static String applyPapi(CommandSender sender, String text) {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            return text;

        return PlaceholderAPI.setPlaceholders(sender instanceof Player ? (Player) sender : null, text);
    }

    @Override
    public void enable() {
        final File file = FileUtils.createFile(this.plugin, "messages.yml");
        this.config = YamlConfiguration.loadConfiguration(file);

        // Set any values that dont exist
        Arrays.stream(Messages.values()).forEach(msg -> {
            final String key = msg.name().toLowerCase().replace("_", "-");
            if (config.get(key) == null)
                config.set(key, msg.value);
        });

        try {
            this.config.save(file);
        } catch (IOException ignored) {
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * Send a configuration message without any placeholders
     *
     * @param receiver  The CommandSender who receives the message.
     * @param messageId The message path
     */
    public void send(CommandSender receiver, String messageId) {
        this.send(receiver, messageId, StringPlaceholders.empty());
    }

    /**
     * Send a configuration messageId with placeholders.
     *
     * @param receiver     The CommandSender who receives the messageId.
     * @param messageId    The messageId path
     * @param placeholders The Placeholders
     */
    public void send(CommandSender receiver, String messageId, StringPlaceholders placeholders) {
        final String msg = this.getConfig().getString(messageId);

        if (msg == null) {
            receiver.sendMessage(colorify("&c&lError &7| &fThis is an invalid message in the messages file, Please contact the server owner about this issue. (Id: " + messageId + ")"));
            return;
        }

        final String prefix = this.getConfig().getString("prefix");
        receiver.sendMessage(colorify(prefix + apply(receiver instanceof Player ? receiver : null, placeholders.apply(msg))));
    }

    /**
     * Send a raw message to the receiver without any placeholders
     * <p>
     * Use this to send a message to a player without the message being defined in a config.
     *
     * @param receiver The message receiver
     * @param message  The raw message
     */
    public void sendRaw(CommandSender receiver, String message) {
        this.sendRaw(receiver, message, StringPlaceholders.empty());
    }

    /**
     * Send a raw message to the receiver with placeholders.
     * <p>
     * Use this to send a message to a player without the message being defined in a config.
     *
     * @param receiver     The message receiver
     * @param message      The message
     * @param placeholders Message Placeholders.
     */
    public void sendRaw(CommandSender receiver, String message, StringPlaceholders placeholders) {
        receiver.sendMessage(colorify(apply(receiver instanceof Player ? receiver : null, placeholders.apply(message))));
    }

    public String get(String message) {
        return colorify(this.config.getString(message) != null ? this.config.getString(message) : Messages.valueOf(message.replace("-", "_")).value);
    }

    @Override
    public void disable() {

    }

    public String apply(CommandSender sender, String text) {
        return applyPapi(sender, text);
    }

    public enum Messages {
        PREFIX("#99ff99&lSkyHoppers &8| &f"),
        GIVEN_HOPPER("You have been given x%amount% #99ff99Sky Hopper&f!"),
        PLACED_HOPPER("You have placed a #99ff99Sky Hopper&f!"),
        DESTROYED_HOPPER("You have destroyed a #99ff99Sky Hopper&f!"),
        NOT_A_HOPPER("This block is not a #99ff99Sky Hopper&f."),
        NOT_A_CONTAINER("This block is not a valid item container."),
        INCOMPATIBLE_CONTAINER("That container is not a valid container."),
        TOGGLED_ON_VISUALISER("You have #87E878enabled &fthe hopper visualiser!"),
        CHANGED_VISUALISER("You changed the hopper visualiser!"),
        TOGGLED_OFF_VISUALISER("You have #FF4F58disabled &fthe hopper visualiser!"),
        LINKED_CONTAINER("You have successfully #87E878linked &fa container!"),
        UNLINKED_CONTAINER("You have successfully #FF4F58unlinked &fa container!"),

        RELOAD("You have reloaded SkyHoppers!"),
        DISABLED_WORLD("You cannot do this in this world."),
        NO_PERM("You do not have permission to do this."),
        INVALID_PLAYER("Please provide a correct player name."),
        INVALID_ARGS("Please use the correct command usage, %usage%"),
        INVALID_AMOUNT("&fPlease provide a valid number."),
        UNKNOWN_CMD("&fPlease include a valid command."),
        PLAYER_ONLY("&fOnly a player can execute this command."),
        CONSOLE_ONLY("&fOnly console can execute this command.");

        private final String value;

        Messages(final String value) {
            this.value = value;
        }

    }
}
