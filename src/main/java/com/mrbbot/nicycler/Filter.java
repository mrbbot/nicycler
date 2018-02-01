package com.mrbbot.nicycler;

/**
 * Interface for filtering values
 * @param <D> type of values to filter
 */
public interface Filter<D> {
    /**
     * Function for filtering a value
     * @param d value to filter
     * @return whether the item matches the filter
     */
    boolean accept(D d);
}
