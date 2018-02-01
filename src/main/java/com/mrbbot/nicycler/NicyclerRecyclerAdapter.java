package com.mrbbot.nicycler;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

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

    ArrayList<D> getFilteredDataset() {
        if(filter == null) return dataset;
        ArrayList<D> filteredDataset = new ArrayList<>();
        for(D d : dataset) if(filter.accept(d)) filteredDataset.add(d);
        return filteredDataset;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        V v = listener.onCreate(parent);
        v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(NicyclerRecyclerAdapter.VH holder, int position) {
        ArrayList<D> filteredDataset = getFilteredDataset();
        holder.d = filteredDataset.get(position);
        //noinspection unchecked
        listener.onBind((V) holder.itemView, filteredDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return getFilteredDataset().size();
    }
}
