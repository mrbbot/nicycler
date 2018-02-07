package com.mrbbot.nicycler;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * The actual {@code RecyclerView.Adapter} of the nicycler
 * @param <D> data type of the nicycler
 * @param <V> view type of the nicycler
 */
class NicyclerRecyclerAdapter<D, V extends View> extends RecyclerView.Adapter<NicyclerRecyclerAdapter.VH> {
    ArrayList<D> dataset;
    private NicyclerListener<D, V> listener;
    @Nullable
    Filter<D> filter;
    Comparator<D> sorter;

    NicyclerRecyclerAdapter(NicyclerListener<D, V> listener) {
        this.dataset = new ArrayList<>();
        this.listener = listener;
    }

    class VH extends RecyclerView.ViewHolder {
        D d;
        VH(V itemView) {
            super(itemView);
        }
    }

    private ArrayList<D> filteredSortedCache;
    ArrayList<D> getFilteredSortedDataset() {
        if(filteredSortedCache != null) return filteredSortedCache;
        ArrayList<D> filteredSortedDataset = new ArrayList<>();

        if(filter == null) {
            filteredSortedDataset.addAll(dataset);
        } else {
            for (D d : dataset) if (filter.accept(d)) filteredSortedDataset.add(d);
        }

        if(sorter != null) {
            Collections.sort(filteredSortedDataset, sorter);
        }

        filteredSortedCache = filteredSortedDataset;
        return filteredSortedDataset;
    }

    void invalidateFilteredSortedCache() {
        filteredSortedCache = null;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        V v = listener.onCreate(parent);
        v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(NicyclerRecyclerAdapter.VH holder, int position) {
        ArrayList<D> filteredDataset = getFilteredSortedDataset();
        holder.d = filteredDataset.get(position);
        //noinspection unchecked
        listener.onBind((V) holder.itemView, filteredDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return getFilteredSortedDataset().size();
    }
}
