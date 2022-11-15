package xyz.oribuin.skyhoppers.hook.protection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.lists.Flags;

public class BentoBoxHook implements ProtectionHook {

    @Override
    public boolean canBuild(Player player, Location location) {
        var optionalIsland = BentoBox.getInstance().getIslands().getIslandAt(location);
        if (optionalIsland.isEmpty())
            return true;

        var island = optionalIsland.get();
        var user = BentoBox.getInstance().getPlayers().getUser(player.getUniqueId());
        return island.isAllowed(user, Flags.BREAK_BLOCKS) && island.isAllowed(user, Flags.PLACE_BLOCKS);
    }

    @Override
    public boolean canOpen(Player player, Location location) {
        var optionalIsland = BentoBox.getInstance().getIslands().getIslandAt(location);
        if (optionalIsland.isEmpty())
            return true;

        var island = optionalIsland.get();
        var user = BentoBox.getInstance().getPlayers().getUser(player.getUniqueId());
        return island.isAllowed(user, Flags.CONTAINER);
    }

}
