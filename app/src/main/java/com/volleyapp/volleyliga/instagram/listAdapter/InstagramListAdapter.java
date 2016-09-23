package com.volleyapp.volleyliga.instagram.listAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.volleyapp.volleyliga.R;
import com.volleyapp.volleyliga.instagram.InstagramEntryModel;

import java.util.ArrayList;
import java.util.List;

public class InstagramListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<InstagramEntryModel> items = new ArrayList<>();
    private Context context;

    private InstagramEntryClickListener clickListener;

    public InstagramListAdapter(Context context) {
        this.context = context;
    }

    public void setInstagramEntryClickListener(InstagramEntryClickListener listener) {
        this.clickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.instagram_entry_view_holder, parent, false);
        return new InstagramEntryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder input, int position) {
        InstagramEntryModel entry = items.get(position);
        InstagramEntryViewHolder viewHolder = (InstagramEntryViewHolder) input;
        viewHolder.setInstagramEntry(entry);/*
        viewHolder.setClickListener(selectedMatch -> {
            if (clickListener != null) {
                clickListener.entryClicked(entry);
            }
        });
*/
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<InstagramEntryModel> data) {
        items = data;
        notifyDataSetChanged();
    }

    public void addItem(InstagramEntryModel item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public List<InstagramEntryModel> getItems() {
        return items;
    }

    public interface InstagramEntryClickListener {
        void entryClicked(InstagramEntryModel entry);
    }

}
