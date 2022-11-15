package xyz.oribuin.skyhoppers.hopper.filter;

import java.util.Arrays;
import java.util.Iterator;

public class FilterOption {

    private FilterType filterType;
    private Iterator<FilterType> iterator;

    public FilterOption(FilterType filterType) {
        this.filterType = filterType;
        this.iterator = Arrays.stream(FilterType.values()).iterator();
        this.iterator.next();
        this.iterator.next();

        while (this.iterator.hasNext()) {
            if (this.iterator.next() == filterType) {
                iterator = Arrays.stream(FilterType.values()).iterator();
                iterator.next();
                break;
            }
        }
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public void setFilterType(FilterType filterType) {
        this.filterType = filterType;
    }

    public Iterator<FilterType> getIterator() {
        return iterator;
    }

    public void setIterator(Iterator<FilterType> iterator) {
        this.iterator = iterator;
    }

}
