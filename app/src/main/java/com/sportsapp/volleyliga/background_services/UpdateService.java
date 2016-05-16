package com.sportsapp.volleyliga.background_services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.models.TeamModel;
import com.sportsapp.volleyliga.models.TimeUnit;
import com.sportsapp.volleyliga.repositories.MatchRepository;
import com.sportsapp.volleyliga.repositories.TeamRepository;
import com.sportsapp.volleyliga.utilities.Preferences;

import org.joda.time.DateTime;

import java.util.List;

import rx.Subscriber;

public class UpdateService extends Service {

    private PowerManager.WakeLock wakeLock;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "UpdateService");
        wakeLock.acquire();

        MatchRepository.setup(getApplicationContext().getFilesDir());
        TeamRepository.initialize(getApplicationContext().getFilesDir());
        MatchRepository.instance.updateAllMatches().subscribe(new Subscriber<MatchModel.Type>() {
            @Override
            public void onCompleted() {
                updateFinished();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(MatchModel.Type type) {

            }
        });
    }

    private void updateFinished() {
        Preferences preferences = Preferences.with(getApplicationContext());
        List<TeamModel> favoriteTeams = TeamRepository.instance.getFavoriteTeams(getApplicationContext());
        if(preferences.getShouldNotifyInAdvance() && favoriteTeams.size() > 0) {
            int timeLength = preferences.getNotifyInAdvanceTimeLength();
            TimeUnit timeUnit = preferences.getNotifyInAdvanceTimeUnit();
            DateTime now = new DateTime();
            for (TeamModel team : favoriteTeams) {
                List<MatchModel> futureMatchesForTeam = MatchRepository.instance.getFutureMatchesForTeam(team.id);
                for (MatchModel match : futureMatchesForTeam) {
                    //Sanity check
                    if(new DateTime(match.matchDateTime).isBefore(now)){
                        continue;
                    }
                    DateTime notificationTime = new DateTime(match.matchDateTime);
                    switch (timeUnit){
                        case Days:
                            notificationTime = notificationTime.minusDays(timeLength);
                            break;
                        case Hours:
                            notificationTime = notificationTime.minusHours(timeLength);
                            break;
                        case Minutes:
                            notificationTime = notificationTime.minusMinutes(timeLength);
                            break;
                    }

                    Intent notificationIntent = new Intent(this, NotificationDisplayer.class);
                    notificationIntent.putExtra(NotificationDisplayer.NOTIFICATION_ID, match.federationMatchNumber);
                    notificationIntent.putExtra(NotificationDisplayer.NOTIFICATION, NotificationDisplayer.createMatchNotification(getApplicationContext(), match, team));
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, match.federationMatchNumber, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

                   long futureInMillis = notificationTime.getMillis();
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
                }
            }
        }
        wakeLock.release();
        stopSelf();
    }
}
