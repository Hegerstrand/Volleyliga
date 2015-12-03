package com.pocketpalsson.volleyball.views.controllers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pocketpalsson.volleyball.R;
import com.pocketpalsson.volleyball.models.MatchModel;

import java.util.ArrayList;
import java.util.List;

public class MatchListAdapter extends RecyclerView.Adapter<SimpleMatchViewHolder> {
    private List<MatchModel> items = new ArrayList<>();
    private Context context;

    private MatchClickListener clickListener;

    public MatchListAdapter(Context context) {
        // Pass context or other static stuff that will be needed.
        this.context = context;
    }

    public void setMatchClickListener(MatchClickListener listener) {
        this.clickListener = listener;
    }

    @Override
    public SimpleMatchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.match_view_holder, parent, false);
        return new SimpleMatchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SimpleMatchViewHolder viewHolder, int position) {
        MatchModel match = items.get(position);
        viewHolder.setMatchModel(match);
        viewHolder.setClickListener(selectedMatch -> clickListener.matchClicked(match));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<MatchModel> data) {
        items = data;
        notifyDataSetChanged();
    }

    public List<MatchModel> getItems() {
        return items;
    }
}
