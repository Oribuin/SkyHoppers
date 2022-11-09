package xyz.oribuin.skyhoppers.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;

import java.util.List;

public class HopperCommandWrapper extends RoseCommandWrapper {

    public HopperCommandWrapper(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public String getDefaultName() {
        return "skyhoppers";
    }

    @Override
    public List<String> getDefaultAliases() {
        return List.of("sh");
    }

    @Override
    public List<String> getCommandPackages() {
        return List.of("xyz.oribuin.skyhoppers.command.command");
    }

    @Override
    public boolean includeBaseCommand() {
        return true;
    }

    @Override
    public boolean includeHelpCommand() {
        return true;
    }

    @Override
    public boolean includeReloadCommand() {
        return true;
    }

}
