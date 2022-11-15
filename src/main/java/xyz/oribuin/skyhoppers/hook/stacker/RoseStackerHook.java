package xyz.oribuin.skyhoppers.hook.stacker;

import dev.rosewood.rosestacker.api.RoseStackerAPI;
import org.bukkit.entity.Item;

public class RoseStackerHook implements StackerHook {

    @Override
    public int getItemAmount(Item item) {
        var stackedItem = RoseStackerAPI.getInstance().getStackedItem(item);
        if (stackedItem != null)
            return stackedItem.getStackSize();

        return item.getItemStack().getAmount();
    }

    @Override
    public void setItemAmount(Item item, int amount) {
        var stackedItem = RoseStackerAPI.getInstance().getStackedItem(item);
        if (stackedItem != null) {
            stackedItem.setStackSize(amount);
            return;
        }

        item.getItemStack().setAmount(amount);
    }

}
