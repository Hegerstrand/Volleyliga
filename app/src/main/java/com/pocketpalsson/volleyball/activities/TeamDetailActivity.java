package com.pocketpalsson.volleyball.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.koushikdutta.ion.Ion;
import com.pocketpalsson.volleyball.R;
import com.pocketpalsson.volleyball.models.TeamModel;
import com.pocketpalsson.volleyball.presenters.TeamDetailPresenter;
import com.pocketpalsson.volleyball.views.TeamDetailView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TeamDetailActivity extends MvpActivity<TeamDetailView, TeamDetailPresenter> implements TeamDetailView, OnRefreshListener {

    public static final String TEAM_ID = "teamId";
    @Bind(R.id.ivMap)
    public ImageView ivMap;
    @Bind(R.id.ivTeam)
    public ImageView ivTeam;
    @Bind(R.id.tvStadiumAddress)
    public TextView tvStadiumAddress;
    @Bind(R.id.tvStadium)
    public TextView tvStadium;
    @Bind(R.id.collapsing_toolbar)
    public CollapsingToolbarLayout collapsing_toolbar;
    @Bind(R.id.toolbar)
    public Toolbar toolbar;

    public int id;
    private TeamModel team;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_detail);
        id = getIntent().getExtras().getInt(TEAM_ID);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        loadData(false);
    }

    @Override
    public TeamDetailPresenter createPresenter() {
        return new TeamDetailPresenter();
    }

    public void loadData(boolean pullToRefresh) {
        getPresenter().loadTeamData();
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }

    @Override
    public void setTeamModel(TeamModel team) {
        this.team = team;
        toolbar.setTitle(team.name);
        tvStadium.setText(team.stadium);
        tvStadiumAddress.setText(team.stadiumAddress);
        ivTeam.setImageDrawable(ContextCompat.getDrawable(this, team.logoRef));
        Ion.with(ivMap).load(getMapsUri(600, 400, team.lat, team.lon));
    }

    @OnClick(R.id.homePage)
    public void goToHomePage() {
        if (team != null) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(team.homePage));
            startActivity(i);
        }
    }

    @OnClick(R.id.email)
    public void sendEmail() {
        if (team != null) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", team.email, null));
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }
    }

    @OnClick(R.id.phone)
    public void callPhoneNumber() {
        if (team != null) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + team.phoneNumber));
            startActivity(intent);
        }
    }

    @OnClick(R.id.facebook)
    public void openFacebook() {
        if(team == null){
            return;
        }
        String baseUrl = "https://www.facebook.com/" + team.facebookId;
        Uri uri;
        try {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);
            uri = Uri.parse("fb://facewebmodal/f?href=" + baseUrl);
        } catch (Exception e) {
            uri = Uri.parse(baseUrl);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @OnClick(R.id.mapView)
    public void openDirection(){
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" + team.lat + "," + team.lon));
        startActivity(intent);
    }

    public static Intent newFacebookIntent(PackageManager pm, String url) {
        Uri uri;
        try {
            pm.getPackageInfo("com.facebook.katana", 0);
            // http://stackoverflow.com/a/24547437/1048340
            uri = Uri.parse("fb://facewebmodal/f?href=" + url);
        } catch (PackageManager.NameNotFoundException e) {
            uri = Uri.parse(url);
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    public static String getMapsUri(int width, int height, double lat, double lon) {
        Uri.Builder builder = getMapsUriBuilder(width, height);
        builder.appendQueryParameter("zoom", "17");
        builder.appendQueryParameter("center", lat + "," + lon);
        builder.appendQueryParameter("markers", "lineColor:red|" + lat + "," + lon);
        return builder.build().toString();
    }

    public static Uri.Builder getMapsUriBuilder(int imageWidth, int imageHeight) {
        Uri.Builder builder = new Uri.Builder();
        float ratio = (float) imageWidth / imageHeight;
        if (imageWidth > 640 && imageWidth > imageHeight) {
            imageWidth = 640;
            imageHeight = (int) (imageWidth / ratio);
        }
        if (imageHeight > 640 && imageHeight > imageWidth) {
            imageHeight = 640;
            imageWidth = (int) (imageHeight * ratio);
        }
        builder.scheme("https").authority("maps.googleapis.com")
                .appendPath("maps").appendPath("api").appendPath("staticmap")
                .appendQueryParameter("size", imageWidth + "x" + imageHeight);
        return builder;
    }

}
