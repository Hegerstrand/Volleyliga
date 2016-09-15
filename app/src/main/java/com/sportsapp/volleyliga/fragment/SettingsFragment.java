package com.sportsapp.volleyliga.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.common.base.Strings;
import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.activities.MainActivityListener;
import com.sportsapp.volleyliga.models.League;
import com.sportsapp.volleyliga.models.TeamModel;
import com.sportsapp.volleyliga.models.TimeUnit;
import com.sportsapp.volleyliga.repositories.TeamRepository;
import com.sportsapp.volleyliga.utilities.Preferences;
import com.sportsapp.volleyliga.views.NotifyInAdvanceView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsFragment extends Fragment {

    @Bind(R.id.toolbar)
    public Toolbar toolbar;
    @Bind(R.id.favoriteTeams)
    public LinearLayout favoriteTeams;
    @Bind(R.id.tvFavoriteTeams)
    public TextView tvFavoriteTeams;
    @Bind(R.id.notifyInAdvance)
    public LinearLayout notifyInAdvance;
    @Bind(R.id.tvNotifyInAdvance)
    public TextView tvNotifyInAdvance;

    private MainActivityListener activityListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityListener = (MainActivityListener) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        updateTeamText();
        updateNotificationText();
    }

    private void updateTeamText() {
        List<TeamModel> favoriteTeams = TeamRepository.instance.getFavoriteTeams(getActivity());
        Collections.sort(favoriteTeams, new TeamModel.TeamComparator());
        String result = "";
        String sep = "";
        for (TeamModel team : favoriteTeams) {
            result += sep + team.name;
            sep = ", ";
        }
        if (Strings.isNullOrEmpty(result)) {
            result = getActivity().getResources().getString(R.string.no_favorite_team_selected);
        }
        tvFavoriteTeams.setText(result);
    }

    private void updateNotificationText() {
        boolean shouldNotify = Preferences.with(getContext()).getShouldNotifyInAdvance();
        if(shouldNotify){
            int notifyInAdvanceTimeLength = Preferences.with(getContext()).getNotifyInAdvanceTimeLength();
            TimeUnit notifyInAdvanceTimeUnit = Preferences.with(getContext()).getNotifyInAdvanceTimeUnit();
            String result = "" + notifyInAdvanceTimeLength + " " + getResources().getString(TimeUnit.getName(notifyInAdvanceTimeUnit)) + " " + getResources().getString(R.string.before);
            tvNotifyInAdvance.setText(result);
        } else {
            tvNotifyInAdvance.setText(R.string.no_notification_set);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activityListener != null && activityListener.isFragmentShown(this)) {
            activityListener.setupContainerUI(toolbar, R.id.settings);
            ActionBar actionBar = activityListener.getSupportActionbar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setTitle("Settings");
            }
        }
    }

    @OnClick(R.id.notifyInAdvance)
    public void notifyInAdvanceClicked() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.notify_in_advance_title)
                .customView(new NotifyInAdvanceView(getActivity()), true)
                .positiveText(R.string.OK)
                .negativeText(R.string.Cancel)
                .onPositive((dialog, which) -> {
                    NotifyInAdvanceView view = (NotifyInAdvanceView) dialog.getCustomView();
                    view.persist();
                    updateNotificationText();
                })
                .show();
    }

    @OnClick(R.id.favoriteTeams)
    public void favoriteTeamsClicked() {
        List<TeamModel> maleTeams = TeamRepository.instance.getTeams(League.MALE);
        Collections.sort(maleTeams, new TeamModel.TeamComparator());
        List<TeamModel> femaleTeams = TeamRepository.instance.getTeams(League.FEMALE);
        Collections.sort(femaleTeams, new TeamModel.TeamComparator());
        List<TeamModel> teams = new ArrayList<>();
        teams.addAll(maleTeams);
        teams.addAll(femaleTeams);

        List<String> teamNames = new ArrayList<>();
        for (TeamModel team : teams) {
            teamNames.add(team.name);
        }

        List<Integer> selected = new ArrayList<>();
        for (int i = 0; i < teams.size(); i++) {
            TeamModel team = teams.get(i);
            if (TeamRepository.instance.isFavorite(team.id, getActivity())) {
                selected.add(i);
            }
        }
        Integer[] selectedArray = selected.toArray(new Integer[selected.size()]);

        new MaterialDialog.Builder(getActivity())
                .title(R.string.select_favorite_team)
                .items(teamNames)
                .itemsCallbackMultiChoice(selectedArray, (dialog, which, text) -> {
                    /**
                     * If you use alwaysCallMultiChoiceCallback(), which is discussed below,
                     * returning false here won't allow the newly selected check box to actually be selected.
                     * See the limited multi choice dialog example in the sample project for details.
                     **/
                    return true;
                })
                .positiveText(R.string.choose)
                .neutralText(R.string.clear_all)
                .autoDismiss(false)
                .onNeutral((dialog1, which1) -> dialog1.clearSelectedIndices())
                .onPositive((dialog, which) -> {
                    List<Integer> selectedIndices = new ArrayList<>();
                    if(dialog.getSelectedIndices() != null) {
                        selectedIndices = Arrays.asList(dialog.getSelectedIndices());
                    }
                    for (int i = 0; i < teams.size(); i++) {
                        TeamModel team = teams.get(i);
                        if(selectedIndices.contains(i)){
                            TeamRepository.instance.setIsFavoriteTeam(getActivity(), team.id, true);
                        } else {
                            TeamRepository.instance.setIsFavoriteTeam(getActivity(), team.id, false);
                        }
                    }
                    updateTeamText();
                    dialog.dismiss();
                })
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
