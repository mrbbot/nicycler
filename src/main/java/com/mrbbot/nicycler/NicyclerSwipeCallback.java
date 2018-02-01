package com.mrbbot.nicycler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

abstract class NicyclerSwipeCallback extends ItemTouchHelper.SimpleCallback {
    private Drawable rightIcon, leftIcon;
    private int rightWidth, rightHeight,
            leftWidth, leftHeight;

    private int rightColour;
    private int leftColour;

    private ColorDrawable background;

    NicyclerSwipeCallback(Context context, @Nullable NicyclerSwipe leftSwipe, @Nullable NicyclerSwipe rightSwipe) {
        super(0, (leftSwipe != null ? ItemTouchHelper.LEFT : 0) +
                (rightSwipe != null ? ItemTouchHelper.RIGHT : 0));

        if(leftSwipe != null) {
            this.leftIcon = leftSwipe.getIcon(context);
            this.leftWidth = this.leftIcon.getIntrinsicWidth();
            this.leftHeight = this.leftIcon.getIntrinsicHeight();

            this.leftColour = leftSwipe.getColor();
        }
        
        if(rightSwipe != null) {
            this.rightIcon = rightSwipe.getIcon(context);
            this.rightWidth = this.rightIcon.getIntrinsicWidth();
            this.rightHeight = this.rightIcon.getIntrinsicHeight();
            
            this.rightColour = rightSwipe.getColor();
        }

        background = new ColorDrawable();
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        int direction = dX < 0 ? ItemTouchHelper.LEFT : ItemTouchHelper.RIGHT;

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getBottom() - itemView.getTop();

        background.setColor(direction == ItemTouchHelper.RIGHT ? rightColour : leftColour);
        background.setBounds(
                direction == ItemTouchHelper.RIGHT ? 0 : itemView.getRight() + (int) dX,
                itemView.getTop(),
                direction == ItemTouchHelper.RIGHT ? (int) dX : itemView.getRight(),
                itemView.getBottom()
        );
        background.draw(c);

        int intrinsicWidth = direction == ItemTouchHelper.RIGHT ? rightWidth : leftWidth;
        int intrinsicHeight = direction == ItemTouchHelper.RIGHT ? rightHeight : leftHeight;

        int iconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int iconMargin = (itemHeight - intrinsicHeight) / 2;
        int iconLeft = direction == ItemTouchHelper.RIGHT ? iconMargin : itemView.getRight() - iconMargin - intrinsicWidth;
        int iconRight = direction == ItemTouchHelper.RIGHT ? iconMargin + intrinsicWidth : itemView.getRight() - iconMargin;
        int iconBottom = iconTop + intrinsicHeight;

        if(Math.abs(dX) > iconMargin) {
            Drawable icon = direction == ItemTouchHelper.RIGHT ? rightIcon : leftIcon;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            icon.draw(c);
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
