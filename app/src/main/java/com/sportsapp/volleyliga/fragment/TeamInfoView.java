package com.sportsapp.volleyliga.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.models.LeagueStandingModel;
import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.models.TeamModel;
import com.sportsapp.volleyliga.repositories.LeagueStandingRepository;
import com.sportsapp.volleyliga.utilities.BusProvider;
import com.sportsapp.volleyliga.utilities.Util;
import com.sportsapp.volleyliga.utilities.busEvents.OpenMatchEvent;
import com.sportsapp.volleyliga.views.FutureMatchView;
import com.sportsapp.volleyliga.views.MatchWithScoreView;
import com.sportsapp.volleyliga.views.MenuItem;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TeamInfoView extends LinearLayout {

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
    @Bind(R.id.nextMatch)
    public FutureMatchView nextMatchView;
    @Bind(R.id.nextMatchCard)
    public CardView nextMatchCard;
    @Bind(R.id.lastMatch)
    public MatchWithScoreView lastMatchView;
    @Bind(R.id.lastMatchCard)
    public CardView lastMatchCard;
    @Bind(R.id.tvWonGames)
    public TextView tvWonGames;
    @Bind(R.id.tvLostGames)
    public TextView tvLostGames;
    @Bind(R.id.tvRanking)
    public TextView tvRanking;
    @Bind(R.id.tvPoints)
    public TextView tvPoints;
    @Bind(R.id.leagueStandingCard)
    public CardView leagueStandingCard;


    private boolean initialized = false;
    private MatchModel lastMatch, nextMatch;
    private LeagueStandingModel standing;

    public TeamInfoView(Context context) {
        super(context);

        setOrientation(VERTICAL);
        inflate(getContext(), R.layout.team_info_view, this);
        ButterKnife.bind(this);
        initialized = true;
    }

    public void setTeamModel(TeamModel team) {
        this.team = team;
        standing = LeagueStandingRepository.instance.getStanding(team.name);
        updateUI();
    }

    private void updateUI() {
        if (initialized && team != null) {
            homePage.setValue(formatHomePage(team.homePage));
            int homePageVisibility = Util.isNullOrEmpty(team.homePage) ? View.GONE : View.VISIBLE;
            homePage.setVisibility(homePageVisibility);
            homePageDivider.setVisibility(homePageVisibility);
            email.setValue(team.email);
            int emailVisibility = Util.isNullOrEmpty(team.email) ? View.GONE : View.VISIBLE;
            email.setVisibility(emailVisibility);
            emailDivider.setVisibility(emailVisibility);
            phone.setValue(formatPhoneNumber(team.phoneNumber));
            int phoneVisibility = Util.isNullOrEmpty(team.phoneNumber) ? View.GONE : View.VISIBLE;
            phone.setVisibility(phoneVisibility);
            phoneDivider.setVisibility(phoneVisibility);
            tvStadium.setText(team.stadium);
            tvStadium.setVisibility(Util.isNullOrEmpty(team.stadium) ? View.GONE : View.VISIBLE);
            tvStadiumAddress.setText(team.stadiumAddress);
            tvStadiumAddress.setVisibility(Util.isNullOrEmpty(team.stadiumAddress) ? View.GONE : View.VISIBLE);
            Ion.with(ivMap).load(getMapsUri(600, 400, team.lat, team.lon));

            updateStanding();
            updateMatches();
        }
    }

    private String formatHomePage(String homePage) {
        String result = homePage.replace("http://", "").replace("https://", "");
        if (result.charAt(result.length() - 1) == '/') {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private String formatPhoneNumber(String phoneNumber) {
        if(phoneNumber.length() < 8){
            return "";
        }
        return String.format("+45 %s %s %s %s", phoneNumber.substring(0, 2), phoneNumber.substring(2, 4), phoneNumber.substring(4, 6), phoneNumber.substring(6, 8));
    }

    private void updateStanding() {
        if (leagueStandingCard == null) {
            return;
        }
        if (standing == null) {
            leagueStandingCard.setVisibility(View.GONE);
        } else {
            leagueStandingCard.setVisibility(View.VISIBLE);
            tvWonGames.setText("" + standing.matchesWon);
            tvLostGames.setText("" + standing.matchesLost);
            tvRanking.setText(standing.getPositionText());
            tvPoints.setText("" + standing.points);
        }
    }

    private void updateMatches() {
        if (lastMatchView != null) {
            if (lastMatch != null) {
                lastMatchCard.setVisibility(View.VISIBLE);
                lastMatchView.setMatchModel(lastMatch, false);
            } else {
                lastMatchCard.setVisibility(View.GONE);
            }
        }
        if (nextMatchView != null) {
            if (nextMatch != null) {
                nextMatchCard.setVisibility(View.VISIBLE);
                nextMatchView.setMatchModel(nextMatch, false);
            } else {
                nextMatchCard.setVisibility(View.GONE);
            }
        }
    }

    public void setMatches(List<MatchModel> matches) {
        if (matches.size() == 0) {
            return;
        }
        lastMatch = null;
        nextMatch = null;
        for (MatchModel match : matches) {
            if (match.getType() == MatchModel.Type.PAST) {
                lastMatch = match;
            } else {
                nextMatch = match;
                break;
            }
        }
        updateMatches();
    }

    @OnClick(R.id.lastMatchCard)
    public void lastMatchClicked() {
        if (lastMatch != null) {
            openMatch(lastMatch.federationMatchNumber);
        }
    }

    @OnClick(R.id.nextMatchCard)
    public void nextMatchClicked() {
        if (nextMatch != null) {
            openMatch(nextMatch.federationMatchNumber);
        }
    }

    public void openMatch(int federationMatchNumber) {
        BusProvider.getInstance().post(new OpenMatchEvent(federationMatchNumber));
    }

    @OnClick(R.id.homePage)
    public void goToHomePage() {
        if (team != null) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(team.homePage));
            getContext().startActivity(i);
        }
    }

    @OnClick(R.id.email)
    public void sendEmail() {
        if (team != null) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", team.email, null));
            getContext().startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }
    }

    @OnClick(R.id.phone)
    public void callPhoneNumber() {
        if (team != null) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + team.phoneNumber));
            getContext().startActivity(intent);
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
            getContext().getPackageManager().getPackageInfo("com.facebook.katana", 0);
            uri = Uri.parse("fb://facewebmodal/f?href=" + baseUrl);
        } catch (Exception e) {
            uri = Uri.parse(baseUrl);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        getContext().startActivity(intent);
    }

    @OnClick(R.id.mapView)
    public void openDirection() {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" + team.lat + "," + team.lon + "&mode=driving"));
        getContext().startActivity(intent);
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
