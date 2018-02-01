package com.mrbbot.nicycler;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

/**
 * Class containing information for how to handle swiping an item
 * @param <D> data type of the nicycler
 */
public abstract class NicyclerSwipe<D> {
    private final int iconId;
    private final String colour;
    final boolean alwaysUpdate;

    /**
     * @param iconId resource id of the icon to be used
     * @param color hex value of the background color to be used
     */
    @SuppressWarnings("WeakerAccess")
    public NicyclerSwipe(int iconId, String color) {
        this(iconId, color, true);
    }

    /**
     * @param iconId resource id of the icon to be used
     * @param color hex value of the background color to be used
     * @param alwaysUpdate whether the recycler view should always update the item when swiped
     */
    @SuppressWarnings("WeakerAccess")
    public NicyclerSwipe(int iconId, String color, boolean alwaysUpdate) {
        this.iconId = iconId;
        this.colour = color;
        this.alwaysUpdate = alwaysUpdate;
    }

    /**
     * Gets the icon as a {@code Drawable}
     * @param context context in which to load the icon
     * @return icon as a {@code Drawable}
     */
    final Drawable getIcon(Context context) {
        return ContextCompat.getDrawable(context, iconId);
    }

    /**
     * Parses the hex color
     * @return integer form of the color
     */
    final int getColor() {
        return Color.parseColor(colour);
    }

    /**
     * Determines whether an item can be swiped
     * @param d data of the item to be swiped
     * @return whether the item can be swiped
     */
    public boolean canSwipe(D d) { return true; }

    /**
     * Processes the swipe of an item
     * @param d data to be swiped
     * @param callback callback to be called when the item is to be updated
     */
    public abstract void swipe(D d, Callback callback);
}
