package com.sportsapp.volleyliga.background_services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.sportsapp.volleyliga.R;
import com.sportsapp.volleyliga.activities.MainActivity;
import com.sportsapp.volleyliga.models.MatchModel;
import com.sportsapp.volleyliga.models.TeamModel;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class NotificationDisplayer extends BroadcastReceiver {

    private static final String POSTPONE_NOTIFICATION = "postpone";
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    public static final String FEDERATION_MATCH_NUMBER = "federationMatchNumber";

    public void onReceive(Context context, Intent intent) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "NotificationDisplayer");
        wakeLock.acquire();
        String action = intent.getAction();
        if (action != null && action.equalsIgnoreCase(POSTPONE_NOTIFICATION)) {

        } else {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = intent.getParcelableExtra(NOTIFICATION);
            int id = intent.getIntExtra(NOTIFICATION_ID, 0);
            notificationManager.notify(id, notification);
        }
        wakeLock.release();
    }

    public static Notification createMatchNotification(Context context, MatchModel match, TeamModel favoriteTeam) {
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.putExtra(MainActivity.FEDERATION_MATCH_NUMBER, match.federationMatchNumber);
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(mainIntent);
        PendingIntent mainPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        //set intents and pending intents to call service on click of "snooze" action button of notification
        Intent postponeIntent = new Intent(context, NotificationDisplayer.class).setAction(POSTPONE_NOTIFICATION).putExtra(FEDERATION_MATCH_NUMBER, match.federationMatchNumber);
        PendingIntent piSnooze = PendingIntent.getBroadcast(context, 0, postponeIntent, 0);


        TeamModel otherTeam;
        if (match.teamHome.id == favoriteTeam.id) {
            otherTeam = match.teamGuest;
        } else {
            otherTeam = match.teamHome;
        }

        String text = "%s are playing %s at %s in %s";
        SimpleDateFormat timestampFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String timeText = timestampFormat.format(match.matchDateTime);

        LocalDate now = new LocalDate();
        LocalDate matchtime = new LocalDate(match.matchDateTime);

        int daysBetween = Days.daysBetween(now, matchtime).getDays();
        if (daysBetween == 0) {
            timeText += " today";
        } else if (daysBetween == 1) {
            timeText += " tomorrow";
        } else {
            timeText += " on " + matchtime.toString("dd.MMM");
        }

        String contentText = String.format(text, favoriteTeam.name, otherTeam.name, timeText, match.stadium);

        String title = favoriteTeam.name + " - " + otherTeam.name;

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle().bigText(contentText);

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), favoriteTeam.logoRef);
        return new NotificationCompat.Builder(context)
                .setStyle(bigTextStyle)
                .setSmallIcon(R.drawable.volleyliga_action_icon)
                .setLargeIcon(icon)
                .setColor(ContextCompat.getColor(context, R.color.primary))
                .setContentTitle(title)
                .setContentText(contentText)
                .setDefaults(Notification.DEFAULT_ALL) // must requires VIBRATE permission
                .setPriority(NotificationCompat.PRIORITY_HIGH) //must give priority to High, Max which will considered as heads-up notification
                .setWhen(match.matchDateTime.getTime())
                .setContentIntent(mainPendingIntent)
                .addAction(R.drawable.ic_update_white_24dp, context.getString(R.string.postpone), piSnooze)
                .build();
    }
}
