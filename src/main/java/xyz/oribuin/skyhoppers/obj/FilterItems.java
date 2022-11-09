package xyz.oribuin.skyhoppers.obj;

import org.bukkit.Material;

import java.util.List;

public class FilterItems {

    private List<String> filterItems;

    public FilterItems(List<String> filterItems) {
        this.filterItems = filterItems;
    }

    public List<String> getFilterItems() {
        return filterItems;
    }

    public void setFilterItems(List<String> filterItems) {
        this.filterItems = filterItems;
    }

}
