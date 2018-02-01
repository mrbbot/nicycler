package com.mrbbot.nicycler;

import android.view.View;

/**
 * Adapter for the {@code NicyclerListener} interface
 * @param <D> data type of the nicycler
 * @param <V> view type of the nicycler
 */
public abstract class NicyclerAdapter<D, V extends View> implements NicyclerListener<D, V> {
    /**
     * Method for binding data to the nicycler
     * @param view view to bind to
     * @param data data to be bound to the view
     */
    @Override
    public void onBind(V view, D data) { }
}
