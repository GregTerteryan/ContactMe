package custom;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.contactme.R;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String NOTIFICATION_CHANNEL_ID = "event_channel_id";
    private static final String NOTIFICATION_CHANNEL_NAME = "event_channel_name";

    @Override
    public void onReceive(Context context, Intent intent) {
        String eventName = intent.getStringExtra("eventName");
        String eventInfo = intent.getStringExtra("eventInfo");
        int eventId = intent.getIntExtra("eventId", 0);

        // Create and show the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(eventName)
                .setContentText(eventInfo)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        NotificationChannel channel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        notificationManager.createNotificationChannel(channel);

        if (ActivityCompat.checkSelfPermission(MyApp.getAppContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Toast toast = Toast.makeText(MyApp.getAppContext(), "Please allow notification for this app", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        else {
            notificationManager.notify(eventId, builder.build());
        }
    }
}
