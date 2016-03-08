package com.sportsapp.volleyliga.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.models.TeamModel;
import com.sportsapp.volleyliga.repositories.TeamRepository;
import com.sportsapp.volleyliga.utilities.Util;
import com.sportsapp.volleyliga.views.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TeamInfoFragment extends Fragment {

    private static final String ARG_TEAM_ID = "teamID";

    public int teamId;
    public TeamModel team;

    @Bind(R.id.ivMap)
    public ImageView ivMap;
    //    @Bind(R.id.ivTeam)
//    public ImageView ivTeam;
    @Bind(R.id.tvStadiumAddress)
    public TextView tvStadiumAddress;
    @Bind(R.id.tvStadium)
    public TextView tvStadium;
    @Bind(R.id.homePage)
    public MenuItem homePage;
    @Bind(R.id.homePageDivider)
    public View homePageDivider;
    @Bind(R.id.email)
    public MenuItem email;
    @Bind(R.id.emailDivider)
    public View emailDivider;
    @Bind(R.id.phone)
    public MenuItem phone;
    @Bind(R.id.phoneDivider)
    public View phoneDivider;


    private boolean initialized = false;

    public static TeamInfoFragment newInstance(int teamID) {
        TeamInfoFragment myFragment = new TeamInfoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TEAM_ID, teamID);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(ARG_TEAM_ID)) {
            teamId = arguments.getInt(ARG_TEAM_ID);
            team = TeamRepository.instance.getTeam(teamId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_team_info, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        initialized = true;
        updateUI();
    }

    public void setTeamModel(TeamModel team) {
        this.team = team;
        if (team != null) {
            updateUI();
        }
    }

    private void updateUI() {
        if (initialized) {
            homePage.setValue(team.homePage);
            int homePageVisibility = Util.isNullOrEmpty(team.homePage) ? View.GONE : View.VISIBLE;
            homePage.setVisibility(homePageVisibility);
            homePageDivider.setVisibility(homePageVisibility);
            email.setValue(team.email);
            int emailVisibility = Util.isNullOrEmpty(team.email) ? View.GONE : View.VISIBLE;
            email.setVisibility(emailVisibility);
            emailDivider.setVisibility(emailVisibility);
            phone.setValue(team.phoneNumber);
            int phoneVisibility = Util.isNullOrEmpty(team.phoneNumber) ? View.GONE : View.VISIBLE;
            phone.setVisibility(phoneVisibility);
            phoneDivider.setVisibility(phoneVisibility);
            tvStadium.setText(team.stadium);
            tvStadium.setVisibility(Util.isNullOrEmpty(team.stadium) ? View.GONE : View.VISIBLE);
            tvStadiumAddress.setText(team.stadiumAddress);
            tvStadiumAddress.setVisibility(Util.isNullOrEmpty(team.stadiumAddress) ? View.GONE : View.VISIBLE);
            Ion.with(ivMap).load(getMapsUri(600, 400, team.lat, team.lon));
        }
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
        if (team == null) {
            return;
        }
        String baseUrl = "https://www.facebook.com/" + team.facebookId;
        Uri uri;
        try {
            getActivity().getPackageManager().getPackageInfo("com.facebook.katana", 0);
            uri = Uri.parse("fb://facewebmodal/f?href=" + baseUrl);
        } catch (Exception e) {
            uri = Uri.parse(baseUrl);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @OnClick(R.id.mapView)
    public void openDirection() {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" + team.lat + "," + team.lon + "&mode=driving"));
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
