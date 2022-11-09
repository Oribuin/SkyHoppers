package xyz.oribuin.skyhoppers.locale;

import dev.rosewood.rosegarden.locale.Locale;

import java.util.LinkedHashMap;
import java.util.Map;

public class EnglishLocale implements Locale {

    @Override
    public String getLocaleName() {
        return "en_US";
    }

    @Override
    public String getTranslatorName() {
        return "Oribuin";
    }

    @Override
    public Map<String, Object> getDefaultLocaleValues() {
        return new LinkedHashMap<>() {{
            this.put("#1", "General Messages");
            this.put("prefix", "#99ff99&lSkyHoppers &8| &f");

            this.put("#2", "Base Command");
            this.put("base-command-color", "#99ff99");
            this.put("base-command-help", "&fUse #99ff99/hopper help &ffor command information.");

            this.put("#3", "Reload Command");
            this.put("command-reload-description", "Reloads the plugin.");
            this.put("command-reload-reloaded", "Configuration and locale files were reloaded");

            this.put("#4", "Help Command");
            this.put("command-help-title", "&fAvailable Commands:");
            this.put("command-help-description", "Displays the help menu.");
            this.put("command-help-list-description", "&8 - #99ff99/%cmd% %subcmd% %args% &7- %desc%");
            this.put("command-help-list-description-no-args", "&8 - #99ff99/%cmd% %subcmd% &7- %desc%");

            this.put("#5", "Give Command");
            this.put("command-give-description", "Give a player a hopper.");
            this.put("command-give-success", "You have given #99ff99%player% &fa x#99ff99%amount% &fSkyHopper.");
            this.put("command-give-received", "You have received a #99ff99x%amount% SkyHopper.");

            this.put("#6", "Giveall Command");
            this.put("command-giveall-description", "Give all players a hopper.");
            this.put("command-giveall-success", "You have given all #99ff99%players%&f  players a x#99ff99%amount% &fSkyHopper.");
            this.put("command-giveall-received", "You have received a #99ff99x%amount% SkyHopper.");

            this.put("#7", "View Command");
            this.put("command-view-description", "Visualize a hopper.");
            this.put("command-view-success", "You are now viewing a hopper.");
            this.put("command-view-disabled", "You are no longer viewing a hopper.");
            this.put("command-view-no-block", "You must be looking at a hopper to view it.");

            this.put("#8", "Generic Command Messages");
            this.put("no-permission", "You don't have permission to execute this.");
            this.put("only-player", "This command can only be executed by a player.");
            this.put("unknown-command", "Unknown command, use #00B4DB/%cmd%&f help for more info");
            this.put("unknown-command-error", "&cAn unknown error occurred; details have been printed to console. Please contact a server administrator.");
            this.put("invalid-subcommand", "&cInvalid subcommand.");
            this.put("invalid-argument", "&cInvalid argument: %message%.");
            this.put("invalid-argument-null", "&cInvalid argument: %name% was null.");
            this.put("missing-arguments", "&cMissing arguments, &b%amount% &crequired.");
            this.put("missing-arguments-extra", "&cMissing arguments, &b%amount%+ &crequired.");

            this.put("#9", "Hopper Messages");
            this.put("hopper-cannot-build", "You cannot place a sky hopper here.");
            this.put("hopper-cannot-open", "You cannot open this hopper here.");
            this.put("hopper-already-placed", "There is already a sky hopper here.");
            this.put("hopper-placed-success", "You have placed a sky hopper.");
            this.put("hopper-removed-success", "You have destroyed a sky hopper.");
            this.put("hopper-linked-success", "You have linked a sky hopper to a container.");
            this.put("hopper-unlinked-success", "You have unlinked a sky hopper from a container.");

            this.put("#10", "Argument Handler Error Messages");
            this.put("argument-handler-integer", "Integer [%input%] must be a whole number between -2^31 and 2^31-1 inclusively");
            this.put("argument-handler-player", "No Player with the username [%input%] was found online");
        }};

    }

}
