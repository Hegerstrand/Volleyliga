package com.sportsapp.volleyliga.views.controllers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.models.MatchModel;

import java.util.ArrayList;
import java.util.List;

public class MatchListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FUTURE_MATCH = 2;
    private static final int MATCH_WITH_SCORE = 1;

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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == MATCH_WITH_SCORE){
            View itemView = inflater.inflate(R.layout.match_view_holder, parent, false);
            return new MatchWithScoreViewHolder(itemView);
        } else if(viewType == FUTURE_MATCH){
            View itemView = inflater.inflate(R.layout.match_future_view_holder, parent, false);
            return new MatchFutureViewHolder(itemView);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        MatchModel match = items.get(position);
        if(match.isInFuture()){
            return FUTURE_MATCH;
        }
        return MATCH_WITH_SCORE;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder input, int position) {
        MatchModel match = items.get(position);
        int viewType = getItemViewType(position);
        if(viewType == MATCH_WITH_SCORE){
            MatchWithScoreViewHolder viewHolder = (MatchWithScoreViewHolder) input;
            viewHolder.setMatchModel(match);
            viewHolder.setClickListener(selectedMatch -> {
                if(clickListener != null) {
                    clickListener.matchClicked(match);
                }
            });
        }
        if(viewType == FUTURE_MATCH){
            MatchFutureViewHolder viewHolder = (MatchFutureViewHolder) input;
            viewHolder.setMatchModel(match);
//            viewHolder.setClickListener(selectedMatch -> {
//                if(clickListener != null) {
//                    clickListener.matchClicked(match);
//                }
//            });
        }

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
