package net.skycraftia.skyhoppers.command;

import net.skycraftia.skyhoppers.SkyHoppersPlugin;
import net.skycraftia.skyhoppers.manager.MessageManager;
import org.bukkit.command.CommandSender;
import xyz.oribuin.orilibrary.command.Command;
import xyz.oribuin.orilibrary.command.SubCommand;

import java.util.List;

@Command.Info(
        name = "hoppers",
        playerOnly = false,
        permission = "skyhoppers.use",
        description = "Give a player a SkyHopper",
        usage = "/hoppers",
        subCommands = {SubGive.class, SubManage.class, SubReload.class, SubView.class},
        aliases = "skyhoppers"

)
public class HopperCommand extends Command {

    private final SkyHoppersPlugin plugin = (SkyHoppersPlugin) this.getOriPlugin();
    private final MessageManager msg = this.plugin.getManager(MessageManager.class);

    public HopperCommand(SkyHoppersPlugin plugin) {
        super(plugin);

        this.register(sender -> msg.send(sender, "player-only"), sender -> msg.send(sender, "invalid-permission"));
    }

    @Override
    public void runFunction(CommandSender sender, String label, String[] args) {

        // Run Sub Commands.
        if (args.length > 0) {
            this.runSubCommands(sender, args, x -> this.msg.send(x, "unknown-cmd"), x -> this.msg.send(x, "no-perm"));
            return;
        }

        // Send the help page to the command sender.
        this.getSubCommands().stream()
                .map(SubCommand::getInfo)
                .filter(info -> sender.hasPermission(info.permission()))
                .forEach(info -> msg.sendRaw(sender, msg.get("prefix") + info.usage()));

    }

    @Override
    public List<String> completeString(CommandSender sender, String label, String[] args) {
        return null;
    }

}
