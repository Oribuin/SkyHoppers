package net.skycraftia.skyhoppers.command;

import net.skycraftia.skyhoppers.SkyHoppers;
import net.skycraftia.skyhoppers.manager.MessageManager;
import org.bukkit.command.CommandSender;
import xyz.oribuin.orilibrary.command.SubCommand;

@SubCommand.Info(
        names = {"reload"},
        permission = "skyhoppers.reload",
        usage = "/hoppers reload"
)
public class SubReload extends SubCommand {

    private final SkyHoppers plugin = (SkyHoppers) this.getOriPlugin();
    private final MessageManager msg = this.plugin.getManager(MessageManager.class);

    public SubReload(SkyHoppers plugin, HopperCommand command) {
        super(plugin, command);
    }

    @Override
    public void executeArgument(CommandSender sender, String[] args) {
        this.msg.send(sender, "reload");
        this.plugin.reload();
    }
}
