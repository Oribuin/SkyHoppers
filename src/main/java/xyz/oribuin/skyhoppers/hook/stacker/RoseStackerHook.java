package xyz.oribuin.skyhoppers.hook.stacker;

import dev.rosewood.rosestacker.api.RoseStackerAPI;
import dev.rosewood.rosestacker.stack.StackedItem;
import org.bukkit.entity.Item;
import xyz.oribuin.skyhoppers.hook.StackerHook;

public class RoseStackerHook implements StackerHook {

    @Override
    public String pluginName() {
        return "RoseStacker";
    }

    @Override
    public int getItemAmount(Item item) {
        StackedItem stackedItem = RoseStackerAPI.getInstance().getStackedItem(item);
        if (stackedItem != null)
            return stackedItem.getStackSize();

        return item.getItemStack().getAmount();
    }

    @Override
    public void setItemAmount(Item item, int amount) {
        StackedItem stackedItem = RoseStackerAPI.getInstance().getStackedItem(item);
        if (stackedItem != null) {
            stackedItem.setStackSize(amount);
            return;
        }

        item.getItemStack().setAmount(amount);
    }

}
