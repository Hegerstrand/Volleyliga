package com.volleyapp.volleyliga.views.controllers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.volleyapp.volleyliga.R;
import com.volleyapp.volleyliga.models.LeagueStandingModel;

import java.util.ArrayList;
import java.util.List;

public class LeagueStandingAdapter extends RecyclerView.Adapter<LeagueStandingViewHolder> {
    private List<LeagueStandingModel> items = new ArrayList<>();
    private Context context;

    private LeagueStandingClickListener clickListener;

    public LeagueStandingAdapter(Context context) {
        // Pass context or other static stuff that will be needed.
        this.context = context;
    }

    public void setLeagueStandingClickListener(LeagueStandingClickListener listener) {
        this.clickListener = listener;
    }

    @Override
    public LeagueStandingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.league_standing_view_holder, parent, false);
        return new LeagueStandingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LeagueStandingViewHolder viewHolder, int position) {
        LeagueStandingModel leagueStanding = items.get(position);
        viewHolder.setLeagueStandingModel(leagueStanding);
        viewHolder.setClickListener(standing -> clickListener.leagueStandingClicked(standing));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<LeagueStandingModel> data) {
        items = data;
        notifyDataSetChanged();
    }

    public List<LeagueStandingModel> getItems() {
        return items;
    }
}
