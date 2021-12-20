package xyz.oribuin.skyhoppers.command;

import xyz.oribuin.skyhoppers.SkyHoppersPlugin;
import xyz.oribuin.skyhoppers.manager.MessageManager;
import org.bukkit.command.CommandSender;
import xyz.oribuin.orilibrary.command.Command;
import xyz.oribuin.orilibrary.command.SubCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        final List<String> tabComplete = new ArrayList<>();

        switch (args.length) {
            case 0, 1 -> tabComplete.addAll(this.getSubCommands().stream().map(SubCommand::getInfo).map(info -> info.names()[0]).collect(Collectors.toList()));
            case 2 -> {
                if (args[0].equalsIgnoreCase("give"))
                    return playerList(sender);
            }
            case 3 -> {
                if (args[0].equalsIgnoreCase("give"))
                    tabComplete.add("<amount>");

            }
        }

        return tabComplete;
    }

}
