package com.volleyapp.volleyliga.views.controllers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.volleyapp.volleyliga.R;
import com.volleyapp.volleyliga.models.PlayerStatisticModelWrapper;

import java.util.ArrayList;
import java.util.List;

public class PlayerStatsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<PlayerStatisticModelWrapper> items = new ArrayList<>();
    private Context context;

    public PlayerStatsListAdapter(Context context) {
        // Pass context or other static stuff that will be needed.
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == PlayerStatisticModelWrapper.Type.HEADER.ordinal()){
            View itemView = inflater.inflate(R.layout.player_statistic_header_view_holder, parent, false);
            return new PlayerStatisticHeaderViewHolder(itemView);
        } else if(viewType == PlayerStatisticModelWrapper.Type.STAT_ITEM.ordinal()){
            View itemView = inflater.inflate(R.layout.player_statistic_view_holder, parent, false);
            return new PlayerStatisticViewHolder(itemView);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        PlayerStatisticModelWrapper entry = items.get(position);
        return entry.type.ordinal();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder input, int position) {
        PlayerStatisticModelWrapper entry = items.get(position);
        if(entry.type == PlayerStatisticModelWrapper.Type.HEADER){
            PlayerStatisticHeaderViewHolder viewHolder = (PlayerStatisticHeaderViewHolder) input;
            viewHolder.setEntry(entry);
        }
        if(entry.type == PlayerStatisticModelWrapper.Type.STAT_ITEM){
            PlayerStatisticViewHolder viewHolder = (PlayerStatisticViewHolder) input;
            viewHolder.setEntry(entry);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<PlayerStatisticModelWrapper> data) {
        items = data;
        notifyDataSetChanged();
    }

    public List<PlayerStatisticModelWrapper> getItems() {
        return items;
    }
}
