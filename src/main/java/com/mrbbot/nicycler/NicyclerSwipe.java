package com.mrbbot.nicycler;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;

/**
 * Class containing information for how to handle swiping an item
 * @param <D> data type of the nicycler
 */
public abstract class NicyclerSwipe<D> {
    private int iconId;
    private String colour;
    final boolean alwaysUpdate;

    private SparseArray<Drawable> iconCache;

    @SuppressWarnings("WeakerAccess")
    public NicyclerSwipe() {
        this(0, "#FFFFFF", true);
    }

    /**
     * @param alwaysUpdate whether the recycler view should always update the item when swiped
     */
    @SuppressWarnings("WeakerAccess")
    public NicyclerSwipe(boolean alwaysUpdate) {
        this(0, "#FFFFFF", alwaysUpdate);
    }

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
        this.iconCache = new SparseArray<>();
    }

    /**
     * Gets the icon as a {@code Drawable}
     * @param context context in which to load the icon
     * @return icon as a {@code Drawable}
     */
    final Drawable doGetIcon(Context context, D d) {
        int id = getIcon(d);
        Drawable cached = iconCache.get(id);
        if(cached != null) return cached;
        cached = ContextCompat.getDrawable(context, id);
        iconCache.put(id, cached);
        return cached;
    }

    /**
     * Gets the resource ID of the icon to use
     * @return resource ID of icon
     */
    public int getIcon(D d) {
        return iconId;
    }

    /**
     * Parses the hex color
     * @return integer form of the color
     */
    final int doGetColour(D d) {
        return Color.parseColor(getColour(d));
    }

    /**
     * Gets the hex value of the colour to use
     * @return hex value of colour
     */
    public String getColour(D d) {
        return colour;
    };

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
