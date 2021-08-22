package net.skycraftia.skyhoppers.hook;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface ProtectionHook {

    boolean canBuild(Player player, Location loc);

    boolean canUse(Player player, Location loc);
}
