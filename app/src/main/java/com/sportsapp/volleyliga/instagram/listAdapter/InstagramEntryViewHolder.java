package com.sportsapp.volleyliga.instagram.listAdapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.instagram.InstagramEntryModel;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InstagramEntryViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    @Bind(R.id.tvCaption)
    public TextView tvCaption;
    @Bind(R.id.tvTimestamp)
    public TextView tvTimestamp;
    @Bind(R.id.tvUsername)
    public TextView tvUsername;
    @Bind(R.id.ivProfilePic)
    public ImageView ivProfilePic;
    @Bind(R.id.ivPicture)
    public ImageView ivPicture;



    private ClickListener clickListener;
    private InstagramEntryModel entry;

    public InstagramEntryViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        // We set listeners to the whole item view, but we could also
        // specify listeners for the title or the icon.
        itemView.setOnClickListener(this);
    }

    public void setInstagramEntry(InstagramEntryModel entry) {
        this.entry = entry;
        tvCaption.setText(entry.caption);
        tvTimestamp.setText(getTimeStamp(entry.created));
        tvUsername.setText("@" + entry.username);
        Ion.with(ivPicture).load(entry.imageUrl);
        Ion.with(ivProfilePic).load(entry.userProfilePictureUrl);
    }

    private String getTimeStamp(Date date) {
        int minutes = minuteDifference(date.getTime());
        if(minutes < 120){
            return minutes + " minutes ago";
        }
        int hours = minutes / 60;
        if(hours < 24) {
            return hours + " hours ago";
        }
        return (hours / 24) + " days ago";
    }


    public int minuteDifference(long time1) {
        long diff = System.currentTimeMillis() - time1;
        long diffMinutes = diff / (60 * 1000);
        return (int) diffMinutes;
    }

    public interface ClickListener {
        void onClick(InstagramEntryModel entry);
    }

    /* Setter for listener. */
    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View v) {
        clickListener.onClick(entry);
    }
}