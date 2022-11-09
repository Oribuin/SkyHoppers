package xyz.oribuin.skyhoppers.hook.stacker;

import org.bukkit.entity.Item;

public interface StackerHook {

    /**
     * Get the amount of items in a stack
     *
     * @param item The item to check
     * @return The amount of items in the stack
     */
    int getItemAmount(Item item);

    /**
     * Change the amount of items in a stack
     *
     * @param item   The item to change
     * @param amount The amount to change to
     */
    void setItemAmount(Item item, int amount);

}
