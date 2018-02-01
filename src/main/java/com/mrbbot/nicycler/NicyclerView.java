package com.mrbbot.nicycler;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A nicer version of the {@code RecyclerView}
 * @param <D> data type of the nicycler
 * @param <V> view type of the nicycler
 */
public class NicyclerView<D extends Serializable, V extends View> extends RecyclerView {
    private final static String STATE_PREFIX = "nicycler_";

    private NicyclerRecyclerAdapter<D, V> adapter;

    public NicyclerView(Context context) {
        super(context);
    }

    public NicyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NicyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Initialisation method without swipes
     * @param listener listener for key nicycler events
     */
    public void init(@NonNull NicyclerListener<D, V> listener) {
        init(listener, null, null);
    }

    /**
     * Initialisation method
     * @param listener listener for key nicycler events
     * @param leftSwipe swipe options for swiping left
     * @param rightSwipe swipe options for swiping right
     */
    public void init(@NonNull NicyclerListener<D, V> listener, @Nullable final NicyclerSwipe<D> leftSwipe, @Nullable final NicyclerSwipe<D> rightSwipe) {
        setLayoutManager(new LinearLayoutManager(getContext()));
        addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        adapter = new NicyclerRecyclerAdapter<>(listener);
        setAdapter(adapter);

        NicyclerSwipeCallback swipeCallback = new NicyclerSwipeCallback(getContext(), leftSwipe, rightSwipe) {
            @Override
            public int getSwipeDirs(RecyclerView recyclerView, ViewHolder viewHolder) {
                //noinspection unchecked
                D d = (D) ((NicyclerRecyclerAdapter.VH) viewHolder).d;
                int dirs = 0;
                if(leftSwipe != null && leftSwipe.canSwipe(d)) {
                    dirs += ItemTouchHelper.LEFT;
                }
                if(rightSwipe != null && rightSwipe.canSwipe(d)) {
                    dirs += ItemTouchHelper.RIGHT;
                }
                return dirs;
            }

            @Override
            public void onSwiped(ViewHolder viewHolder, int direction) {
                //noinspection unchecked
                final D d = (D) ((NicyclerRecyclerAdapter.VH) viewHolder).d;

                Callback callback = new Callback() {
                    @Override
                    public void callback() {
                        update(d);
                    }
                };

                if(direction == ItemTouchHelper.LEFT && leftSwipe != null) {
                    if(leftSwipe.alwaysUpdate) update(d);
                    leftSwipe.swipe(d, callback);
                } else if(direction == ItemTouchHelper.RIGHT && rightSwipe != null) {
                    if(rightSwipe.alwaysUpdate) update(d);
                    rightSwipe.swipe(d, callback);
                }
            }
        };
        ItemTouchHelper swipeTouchHelper = new ItemTouchHelper(swipeCallback);
        swipeTouchHelper.attachToRecyclerView(this);
    }

    /**
     * Filters the dataset to only display items matching the filter
     * @param filter filter to match against
     */
    public void filter(@Nullable Filter<D> filter) {
        adapter.filter = filter;
        adapter.notifyDataSetChanged();
    }

    /**
     * Sets the contents of the dataset to the items
     * @param items items to set
     */
    @SafeVarargs
    public final void set(D... items) {
        adapter.dataset.clear();
        Collections.addAll(adapter.dataset, items);
        adapter.notifyDataSetChanged();
    }

    /**
     * Adds the contents of items to the dataset
     * @param items items to add
     */
    @SafeVarargs
    public final void add(D... items) {
        Collections.addAll(adapter.dataset, items);
        adapter.notifyItemRangeInserted(adapter.dataset.size() - items.length, items.length);
    }

    /**
     * Checks if a list contains any values of another list
     * @param list list to check against
     * @param checkList list containing values to check
     * @return if {@param list} contains any of the values in {@param checkList}
     */
    private boolean containsAny(ArrayList<D> list, ArrayList<D> checkList) {
        for(D d : checkList) if(list.contains(d)) return true;
        return false;
    }

    /**
     * Removes items from the dataset that match a filter
     * @param filter filter to match against
     */
    public final void remove(Filter<D> filter) {
        ArrayList<D> toRemove = new ArrayList<>();
        for(int i = 0; i < adapter.dataset.size(); i++) {
            if(filter.accept(adapter.dataset.get(i))) {
                toRemove.add(adapter.dataset.get(i));
            }
        }

        ArrayList<D> filteredDataset = adapter.getFilteredDataset();
        boolean updatingNeeded = containsAny(filteredDataset, toRemove);
        adapter.dataset.removeAll(toRemove);
        if(updatingNeeded) {
            if (toRemove.size() > 1) {
                adapter.notifyDataSetChanged();
            } else if (toRemove.size() == 1) {
                adapter.notifyItemRemoved(filteredDataset.indexOf(toRemove.get(0)));
            }
        }
    }

    /**
     * Updates items from the dataset
     * @param filter filter that items are sent to where they may be updated; if they are
     * {@code true} should be returned, otherwise {@code false} should
     */
    public final void update(Filter<D> filter) {
        ArrayList<D> updated = new ArrayList<>();
        for(int i = 0; i < adapter.dataset.size(); i++) {
            if(filter.accept(adapter.dataset.get(i))) {
                updated.add(adapter.dataset.get(i));
            }
        }

        ArrayList<D> filteredDataset = adapter.getFilteredDataset();
        if(containsAny(filteredDataset, updated)) {
            if (updated.size() > 1) {
                adapter.notifyDataSetChanged();
            } else if (updated.size() == 1) {
                Log.i("NICYCLER", "Single updated!");
                adapter.notifyItemChanged(filteredDataset.indexOf(updated.get(0)));
            }
        }
    }

    /**
     * Finds the index of the specified value in the dataset and triggers an update
     * @param d item to update
     */
    private void update(D d) {
        adapter.notifyItemChanged(adapter.getFilteredDataset().indexOf(d));
    }

    /**
     * Stores the dataset so that it can be restored later using an empty id
     * @param outState bundle to save to
     */
    public void save(Bundle outState) {
        save(outState, "");
    }

    /**
     * Stores the dataset so that it can be restored later using the specified id to differentiate
     * between multiple nicyclers
     * @param outState bundle to save to
     * @param id id to save to
     */
    public void save(Bundle outState, String id) {
        outState.putSerializable(STATE_PREFIX + id, adapter.dataset);
    }

    /**
     * Restores the dataset using an empty id
     * @param savedInstanceState bundle to restore from
     */
    public void restore(Bundle savedInstanceState) {
        restore(savedInstanceState, "");
    }

    /**
     * Restores the dataset using the specified id to differentiate between multiple nicyclers
     * @param savedInstanceState bundle to restore from
     * @param id id to restore from
     */
    @SuppressWarnings("unchecked")
    public void restore(Bundle savedInstanceState, String id) {
        if(savedInstanceState.containsKey(STATE_PREFIX + id)) {
            adapter.dataset = (ArrayList<D>) savedInstanceState.getSerializable(STATE_PREFIX + id);
        }
    }
}
