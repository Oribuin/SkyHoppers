package xyz.oribuin.skyhoppers.hook.stacker;

import com.bgsoftware.wildstacker.api.WildStackerAPI;
import org.bukkit.entity.Item;

public class WildStackerHook implements StackerHook {

    @Override
    public int getItemAmount(Item item) {
        var stackedItem = WildStackerAPI.getStackedItem(item);
        if (stackedItem != null) {
            return stackedItem.getStackAmount();
        }

        return item.getItemStack().getAmount();
    }

    @Override
    public void setItemAmount(Item item, int amount) {
        var stackedItem = WildStackerAPI.getStackedItem(item);
        if (stackedItem != null) {
            stackedItem.setStackAmount(amount, true);
            return;
        }

        item.getItemStack().setAmount(amount);
    }

}
