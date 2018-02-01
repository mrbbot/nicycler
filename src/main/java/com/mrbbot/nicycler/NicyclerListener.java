package com.mrbbot.nicycler;

import android.view.View;
import android.view.ViewGroup;

/**
 * Events for {@code NicyclerView}
 * @param <D> data type of the nicycler
 * @param <V> view type of the nicycler
 */
public interface NicyclerListener<D, V extends View> {
    /**
     * Function to return a view to be placed within the nicycler
     * @param parent parent container
     * @return view to be placed within the nicycler
     */
    V onCreate(ViewGroup parent);

    /**
     * Method for binding data to the nicycler
     * @param view view to bind to
     * @param data data to be bound to the view
     */
    void onBind(V view, D data);
}
