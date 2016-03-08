package com.sportsapp.volleyliga.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.fragment.TeamDetailMatchFragment;
import com.sportsapp.volleyliga.fragment.TeamInfoFragment;
import com.sportsapp.volleyliga.models.TeamModel;
import com.sportsapp.volleyliga.presenters.TeamDetailPresenter;
import com.sportsapp.volleyliga.repositories.TeamRepository;
import com.sportsapp.volleyliga.views.MainActivityView;
import com.sportsapp.volleyliga.views.TeamDetailView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TeamDetailActivity extends MvpActivity<TeamDetailView, TeamDetailPresenter> implements MainActivityView, TeamDetailView {

    public static final String TEAM_ID = "teamId";

    @Bind(R.id.toolbar)
    public Toolbar toolbar;

    @Bind(R.id.viewPager)
    public ViewPager viewPager;
    @Bind(R.id.tabLayout)
    public TabLayout tabLayout;


    public int id;
    private TeamModel team;
    private TeamDetailPagerAdapter pagerAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_detail);
        id = getIntent().getExtras().getInt(TEAM_ID);
        team = TeamRepository.instance.getTeam(id);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        pagerAdapter = new TeamDetailPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        setTeamModel(team);
    }

    @Override
    public TeamDetailPresenter createPresenter() {
        return new TeamDetailPresenter();
    }

    @Override
    public void setTeamModel(TeamModel team) {
        this.team = team;
        getSupportActionBar().setTitle(team.name);
        TeamInfoFragment infoFragment = (TeamInfoFragment) pagerAdapter.getItem(1);
        infoFragment.setTeamModel(team);
        TeamDetailMatchFragment matchFragment = (TeamDetailMatchFragment) pagerAdapter.getItem(0);
        matchFragment.setTeamModel(team);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void openMatch(int federationMatchNumber) {
        Intent intent = new Intent(this, MatchActivity.class);
        intent.putExtra(MatchActivity.FEDERATION_MATCH_NUMBER, federationMatchNumber);
        startActivity(intent);
    }


    public class TeamDetailPagerAdapter extends FragmentPagerAdapter {

        public TeamDetailPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0){
                return "Matches";
            } else if (position == 1){
                return "Information";
            }
            return "";
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return TeamDetailMatchFragment.newInstance(team.id);
            } else if (position == 1) {
                return TeamInfoFragment.newInstance(team.id);
            }
            return null;
        }

    }

}
