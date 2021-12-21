package xyz.oribuin.skyhoppers.hook.stacker;

import com.bgsoftware.wildstacker.api.WildStackerAPI;
import com.bgsoftware.wildstacker.api.objects.StackedItem;
import org.bukkit.entity.Item;
import xyz.oribuin.skyhoppers.hook.StackerHook;

public class WildStackerHook implements StackerHook {
    @Override
    public String pluginName() {
        return "WildStacker";
    }

    @Override
    public int getItemAmount(Item item) {
        StackedItem stackedItem = WildStackerAPI.getStackedItem(item);
        if (stackedItem != null) {
            return stackedItem.getStackAmount();
        }

        return item.getItemStack().getAmount();
    }

    @Override
    public void setItemAmount(Item item, int amount) {
        StackedItem stackedItem = WildStackerAPI.getStackedItem(item);
        if (stackedItem != null) {
            stackedItem.setStackAmount(amount, true);
            return;
        }

        item.getItemStack().setAmount(amount);
    }
}
