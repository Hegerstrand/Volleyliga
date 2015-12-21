package com.pocketpalsson.volleyball.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pocketpalsson.volleyball.R;
import com.pocketpalsson.volleyball.models.SetInfoModel;
import com.pocketpalsson.volleyball.utilities.ContextHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SetFullDetailView extends LinearLayout {

//    @Bind({R.id.tvHeader1, R.id.tvHeader2, R.id.tvHeader3, R.id.tvHeader4, R.id.tvHeader5, R.id.tvHeader6})
//    public List<TextView> tvHeaders;
//    @Bind({R.id.tvHomeSetResult1, R.id.tvHomeSetResult2, R.id.tvHomeSetResult3, R.id.tvHomeSetResult4, R.id.tvHomeSetResult5, R.id.tvHomeSetResult6})
//    public List<TextView> tvHomeViews;
//    @Bind({R.id.tvGuestSetResult1, R.id.tvGuestSetResult2, R.id.tvGuestSetResult3, R.id.tvGuestSetResult4, R.id.tvGuestSetResult5, R.id.tvGuestSetResult6})
//    public List<TextView> tvGuestViews;

    //    @Bind(R.id.headerContainer)
//    public LinearLayout headerContainer;
//    @Bind(R.id.homeContainer)
//    public LinearLayout homeContainer;
//    @Bind(R.id.guestContainer)
//    public LinearLayout guestContainer;
    @Bind(R.id.setContainer)
    public LinearLayout setContainer;
    @Bind(R.id.scoreHome)
    public TextView scoreHome;
    @Bind(R.id.scoreGuest)
    public TextView scoreGuest;



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

    public void setStats(List<SetInfoModel> stats, int setsWonByHome, int setsWonByGuest) {
        setContainer.removeAllViews();
        scoreHome.setText("" + setsWonByHome);
        scoreGuest.setText("" + setsWonByGuest);
        int topMargin = 0;
        int bottomMargin = ContextHelper.dpToPixels(getContext(), 4);
        for (SetInfoModel stat : stats) {
            LargeSetView setView = new LargeSetView(getContext());
            setContainer.addView(setView);
            setView.setStat(stat);
            LayoutParams layoutParams = (LayoutParams) setView.getLayoutParams();
            layoutParams.setMargins(0, topMargin, 0, bottomMargin);
            setView.setLayoutParams(layoutParams);
            topMargin = bottomMargin;
        }
    }
}
