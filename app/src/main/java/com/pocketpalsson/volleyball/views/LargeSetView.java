package com.pocketpalsson.volleyball.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pocketpalsson.volleyball.R;
import com.pocketpalsson.volleyball.models.SetInfoModel;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LargeSetView extends LinearLayout {

    @Bind(R.id.tvHomeTeamScore)
    public TextView tvHomeTeamScore;
    @Bind(R.id.tvGuestTeamScore)
    public TextView tvGuestTeamScore;

    private GradientDrawable backgroundDrawable;

    public LargeSetView(Context context) {
        super(context);
        init();
    }


    public LargeSetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LargeSetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.large_set_view, this);
        backgroundDrawable = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.set_result_background);
        setBackgroundDrawable(backgroundDrawable);
        setOrientation(HORIZONTAL);
        ButterKnife.bind(this);
    }

    public void setStat(SetInfoModel stat) {
        tvHomeTeamScore.setText("" + stat.scoreHome);
        tvGuestTeamScore.setText("" + stat.scoreGuest);
        if(stat.homeWon){
            tvHomeTeamScore.setTypeface(null, Typeface.BOLD);
            tvHomeTeamScore.setTextColor(Color.BLACK);
            tvGuestTeamScore.setTypeface(null, Typeface.NORMAL);
            tvGuestTeamScore.setTextColor(Color.GRAY);
        } else {
            tvHomeTeamScore.setTypeface(null, Typeface.NORMAL);
            tvHomeTeamScore.setTextColor(Color.GRAY);
            tvGuestTeamScore.setTypeface(null, Typeface.BOLD);
            tvGuestTeamScore.setTextColor(Color.BLACK);
        }
//        if(backgroundDrawable != null) {
//            backgroundDrawable.setColor(getResources().getColor(stat.homeWon ? R.color.home_color_light : R.color.guest_color_light));
//        }
    }
}
