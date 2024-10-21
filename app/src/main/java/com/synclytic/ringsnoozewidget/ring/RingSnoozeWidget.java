package com.synclytic.ringsnoozewidget.ring;

import com.synclytic.ringsnoozewidget.R;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;


public class RingSnoozeWidget extends AppWidgetProvider {

    private static final String RAGE_SNOOZE = "com.synclytic.ringsnoozewidget.RAGE_SNOOZE";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            // Get snooze duration from preferences
            SharedPreferences prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE);
            int snoozeDuration = prefs.getInt("snooze_duration_" + appWidgetId, -1);

            if (snoozeDuration != -1) {
                // Select the icon based on snooze duration
                int iconId = getIconIdForSnoozeDuration(snoozeDuration);

                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ring_snooze_widget);
                views.setImageViewResource(R.id.widgetIcon, iconId);

                // Intent to trigger the snooze action
                Intent snoozeIntent = new Intent(context, RingSnoozeWidget.class);
                snoozeIntent.setAction(RAGE_SNOOZE);
                snoozeIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, appWidgetId, snoozeIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

                // Set click listener on the widget's ImageView
                views.setOnClickPendingIntent(R.id.widgetIcon, snoozePendingIntent);

                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
    }

    // Helper function to get icon ID based on snooze duration
    private int getIconIdForSnoozeDuration(int snoozeDuration) {
        switch (snoozeDuration) {
            case 30:
                return R.drawable.widget_logo_30m;
            case 1:
                return R.drawable.widget_logo_1;
            case 2:
                return R.drawable.widget_logo_2;
            case 3:
                return R.drawable.widget_logo_3;
            case 4:
                return R.drawable.widget_logo_4;
            case 8:
                return R.drawable.widget_logo_8;
            case 12:
                return R.drawable.widget_logo_12;
            default:
                return R.drawable.widget_logo_12; // Default icon
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (RAGE_SNOOZE.equals(intent.getAction())) {

            // Get the appWidgetId from the intent
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            // Get snooze duration from preferences
            SharedPreferences prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE);
            int snoozeDuration = prefs.getInt("snooze_duration_" + appWidgetId, -1);

            if (snoozeDuration != -1) {
                // Trigger UI automation with the snooze duration
                triggerRingSnoozeAutomation(context, snoozeDuration);
            }
        }
    }

    private void triggerRingSnoozeAutomation(Context context, int snoozeDuration) {
        // UI Automation code to interact with the Ring app
        // This will depend on the specific UI elements and actions in the Ring app

        // Example using UI Automator TODO need to adapt this
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        try {
            // 1. Open the Ring app
            ComponentName ringApp = new ComponentName("com.ringapp", "com.ringapp.MainActivity"); // Replace with actual component name
            Intent ringIntent = new Intent();
            ringIntent.setComponent(ringApp);
            ringIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(ringIntent);

            // 2. Wait for the app to load TODO figure out and adjust the timeout
            uiDevice.wait(Until.hasObject(By.pkg("com.ringapp").depth(0)), 5000);

            // 3. Find and interact with UI elements to snooze
            //    This will depend on the Ring app's UI and the snoozeDuration

        } catch (Exception e) {
            Log.e("RingSnooze", "Error triggering snooze automation", e);
        }
    }
}