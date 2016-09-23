package com.volleyapp.volleyliga.views;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.volleyapp.volleyliga.R;
import com.volleyapp.volleyliga.models.MatchModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FutureMatchView extends FrameLayout {

    @SuppressWarnings("ResourceType")

    @Bind(R.id.tvHomeTeam)
    public TextView tvHomeTeam;
    @Bind(R.id.ivHomeTeam)
    public ImageView ivHomeTeam;
    @Bind(R.id.tvGuestTeam)
    public TextView tvGuestTame;
    @Bind(R.id.ivGuestTeam)
    public ImageView ivGuestTame;
    @Bind(R.id.tvDate)
    public TextView tvDate;
    @Bind(R.id.tvTime)
    public TextView tvTime;
    @Bind(R.id.tvStadium)
    public TextView tvStadium;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MMM", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public FutureMatchView(Context context) {
        super(context);
        init();
    }

    public FutureMatchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FutureMatchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.match_future_view_holder, this);
        ButterKnife.bind(this, this);
    }

    public void setMatchModel(MatchModel match, boolean addSelectorBackground) {
        int homeTypeface = match.setsWonByHome > match.setsWonByGuest ? Typeface.BOLD : Typeface.NORMAL;
        int guestTypeface = match.setsWonByHome < match.setsWonByGuest ? Typeface.BOLD : Typeface.NORMAL;

        if(addSelectorBackground) {
            setForeground(ContextCompat.getDrawable(getContext(), R.drawable.selectable_item_background));
        } else {
            setForeground(null);
        }

        ivHomeTeam.setImageDrawable(ContextCompat.getDrawable(getContext(), match.teamHome.logoRef));
        tvHomeTeam.setText("" + match.teamHome.name);
        tvHomeTeam.setTypeface(null, homeTypeface);

        ivGuestTame.setImageDrawable(ContextCompat.getDrawable(getContext(), match.teamGuest.logoRef));
        tvGuestTame.setText("" + match.teamGuest.name);
        tvGuestTame.setTypeface(null, guestTypeface);

        String stadiumText = match.stadium;
        if (!match.stadiumCity.equalsIgnoreCase("")) {
            stadiumText += " (" + match.stadiumCity + ")";
        }
        tvStadium.setText(stadiumText);

        tvDate.setText(dateFormat.format(match.matchDateTime).toLowerCase());
        tvTime.setText(timeFormat.format(match.matchDateTime));
    }
}
