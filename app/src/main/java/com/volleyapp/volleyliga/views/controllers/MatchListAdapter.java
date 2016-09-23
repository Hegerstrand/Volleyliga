package com.volleyapp.volleyliga.views.controllers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.volleyapp.volleyliga.models.MatchModel;
import com.volleyapp.volleyliga.views.FutureMatchView;
import com.volleyapp.volleyliga.views.MatchWithScoreView;

import java.util.ArrayList;
import java.util.List;

public class MatchListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FUTURE_MATCH = 2;
    private static final int MATCH_WITH_SCORE = 1;

    private List<MatchModel> items = new ArrayList<>();
    private List<Integer> matchNumbers = new ArrayList<>();
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
        if(viewType == MATCH_WITH_SCORE){
            MatchWithScoreView matchView = new MatchWithScoreView(context);
            return new MatchWithScoreViewHolder(matchView);
        } else if(viewType == FUTURE_MATCH){
            FutureMatchView matchView = new FutureMatchView(context);
            return new MatchFutureViewHolder(matchView);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        MatchModel match = items.get(position);
        if(match.isOnFutureDay()){
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
        items = new ArrayList<>();
        for (MatchModel match : data) {
            items.add(match);
        }
        notifyDataSetChanged();
    }

    public List<MatchModel> getItems() {
        return items;
    }
}
