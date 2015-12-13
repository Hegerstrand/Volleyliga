package com.pocketpalsson.volleyball.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pocketpalsson.volleyball.R;
import com.pocketpalsson.volleyball.models.SetInfoModel;
import com.pocketpalsson.volleyball.utilities.ContextHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SetFullDetailView extends LinearLayout {

    @Bind({R.id.tvHeader1, R.id.tvHeader2, R.id.tvHeader3, R.id.tvHeader4, R.id.tvHeader5, R.id.tvHeader6})
    public List<TextView> tvHeaders;
    @Bind({R.id.tvHomeSetResult1, R.id.tvHomeSetResult2, R.id.tvHomeSetResult3, R.id.tvHomeSetResult4, R.id.tvHomeSetResult5, R.id.tvHomeSetResult6})
    public List<TextView> tvHomeViews;
    @Bind({R.id.tvGuestSetResult1, R.id.tvGuestSetResult2, R.id.tvGuestSetResult3, R.id.tvGuestSetResult4, R.id.tvGuestSetResult5, R.id.tvGuestSetResult6})
    public List<TextView> tvGuestViews;

    @Bind(R.id.headerContainer)
    public LinearLayout headerContainer;
    @Bind(R.id.homeContainer)
    public LinearLayout homeContainer;
    @Bind(R.id.guestContainer)
    public LinearLayout guestContainer;


    public SetFullDetailView(Context context) {
        super(context);
        init();
    }


    public SetFullDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SetFullDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.set_full_detail_view, this);
        setOrientation(VERTICAL);
        ButterKnife.bind(this);
    }

    public void setStats(List<SetInfoModel> stats) {
        int leftMargin = 0;
        int rightMargin = ContextHelper.dpToPixels(getContext(), 2);

        headerContainer.setWeightSum(stats.size());
        homeContainer.setWeightSum(stats.size());
        guestContainer.setWeightSum(stats.size());

        for (int i = 0; i < 6; i++) {
            if(i < stats.size()){
                SetInfoModel setInfo = stats.get(i);
                tvHeaders.get(i).setVisibility(View.VISIBLE);
                TextView homeView = tvHomeViews.get(i);
                TextView guestView = tvGuestViews.get(i);
                homeView.setVisibility(View.VISIBLE);
                homeView.setTypeface(null, setInfo.homeWon ? Typeface.BOLD : Typeface.NORMAL);
                homeView.setText("" + setInfo.scoreHome);
                guestView.setVisibility(View.VISIBLE);
                guestView.setText("" + setInfo.scoreGuest);
                guestView.setTypeface(null, setInfo.homeWon ? Typeface.NORMAL : Typeface.BOLD);
            } else {
                tvHeaders.get(i).setVisibility(View.GONE);
                tvHomeViews.get(i).setVisibility(View.GONE);
                tvGuestViews.get(i).setVisibility(View.GONE);
            }
        }
    }
}
