package xyz.oribuin.skyhoppers.hook;

import org.bukkit.entity.Item;

public interface StackerHook {

    String pluginName();

    int getItemAmount(Item item);

    void setItemAmount(Item item, int amount);
}
