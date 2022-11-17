package xyz.oribuin.skyhoppers.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.AbstractConfigurationManager;
import xyz.oribuin.skyhoppers.SkyHoppersPlugin;

import java.util.List;

public class ConfigurationManager extends AbstractConfigurationManager {

    public ConfigurationManager(RosePlugin rosePlugin) {
        super(rosePlugin, Settings.class);
    }

    public enum Settings implements RoseSetting {
        INSTANT_PICKUP("instant-pickup", false, "Should broken hoppers be instantly put into the player's inventory?"),
        DISABLED_HOOKS("disabled-hooks", List.of("NONE"), "A list of plugins to disable hooks for. [BentoBox, IridiumSkyblock, Lands, Towny, WorldGuard]"),

        // Task Options
        TASKS_TRANSFER("tasks.transfer", 8, "How often in ticks should hoppers transfer items?"),
        TASKS_ITEMS_PER_TRANSFER("tasks.items-per-transfer", 2, "How many items should be transferred per transfer?"),
        TASKS_SUCTION("tasks.suction", 10, "How often in ticks should hoppers suck items?"),
        TASKS_VISUALISER("tasks.visualiser", 5, "How often in ticks should the visualiser particles be sent?"),

        // Hopper Options
        SUCTION_RANGE("suction-range", 10.0, "The range of the hopper's suction"),
        HOPPER_ITEM_NAME("hopper-item.name", "#99ff99&lSky Hopper"),
        HOPPER_ITEM_LORE("hopper-item.lore", List.of(
                "&7Transfer items wirelessly from this hopper",
                "&7to any container & suction items in",
                "&7a 10 block radius",
                " ",
                " &f&m-----------------",
                " &f| #99ff99&lEnabled &7: &f%enabled%",
                " &f| #99ff99&lContainer &7: &f%linked%",
                " &f| #99ff99&lFilter &7: &f%filter_type%",
                " &f&m-----------------"
        )),
        HOPPER_ITEM_GLOW("hopper-item.glow", true, "Should the hopper item glow?"),
        ;

        private final String key;
        private final Object defaultValue;
        private final String[] comments;
        private Object value = null;

        Settings(String key, Object defaultValue, String... comments) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.comments = comments;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public Object getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String[] getComments() {
            return this.comments;
        }

        @Override
        public Object getCachedValue() {
            return this.value;
        }

        @Override
        public void setCachedValue(Object value) {
            this.value = value;
        }

        @Override
        public CommentedFileConfiguration getBaseConfig() {
            return SkyHoppersPlugin.getInstance().getManager(ConfigurationManager.class).getConfig();
        }

    }

    @Override
    protected String[] getHeader() {
        return new String[]{"  ___________            ___ ___                                           ",
                " /   _____/  | _____.__./   |   \\  ____ ______ ______   ___________  ______",
                " \\_____  \\|  |/ <   |  /    ~    \\/  _ \\\\____ \\\\____ \\_/ __ \\_  __ \\/  ___/",
                " /        \\    < \\___  \\    Y    (  <_> )  |_> >  |_> >  ___/|  | \\/\\___ \\ ",
                "/_______  /__|_ \\/ ____|\\___|_  / \\____/|   __/|   __/ \\___  >__|  /____  >",
                "        \\/     \\/\\/           \\/        |__|   |__|        \\/           \\/"};
    }

}
