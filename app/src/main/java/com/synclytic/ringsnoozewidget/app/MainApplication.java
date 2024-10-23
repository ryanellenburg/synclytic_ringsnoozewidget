package com.synclytic.ringsnoozewidget.app;

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
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;


public class MainApplication extends AppWidgetProvider {

    public static final String RAGE_SNOOZE = "com.synclytic.ringsnoozewidget.RAGE_SNOOZE";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {

            // Get snooze duration from preferences
            SharedPreferences prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE);
            int snoozeDuration = prefs.getInt("snooze_duration_" + appWidgetId, -1);

            // Get the icon resource ID based on the snooze duration
            int iconResource = getIconIdForSnoozeDuration(snoozeDuration);

            RemoteViews iconViews = new RemoteViews(context.getPackageName(), R.layout.ring_snooze_widget);

            // Set the icon on the ImageView
            iconViews.setImageViewResource(R.id.widgetIcon, iconResource);

            if (snoozeDuration != -1) {
                // Select the icon based on snooze duration
                int iconId = getIconIdForSnoozeDuration(snoozeDuration);

                RemoteViews widgetViews = new RemoteViews(context.getPackageName(), R.layout.ring_snooze_widget);
                widgetViews.setImageViewResource(R.id.widgetIcon, iconId);

                // Intent to trigger the snooze action
                Intent snoozeIntent = new Intent(context, MainApplication.class);
                snoozeIntent.setAction(RAGE_SNOOZE);
                snoozeIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, appWidgetId, snoozeIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

                // Set click listener on the widget's ImageView
                widgetViews.setOnClickPendingIntent(R.id.widgetIcon, snoozePendingIntent);

                appWidgetManager.updateAppWidget(appWidgetId, widgetViews);
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

        // Using UI Automator
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        try {

            // Step 1: Open the Ring app
            // Package name is com.ringapp
            // Class name from ".\adb shell dumpsys activity activities" search is com.ringapp/.maindashboard.MyDevicesDashboardActivity
            ComponentName ringApp = new ComponentName("com.ringapp", "com.ringapp.maindashboard.MyDevicesDashboardActivity");
            Intent ringIntent = new Intent();
            ringIntent.setComponent(ringApp);
            ringIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(ringIntent);

            // Wait for the app to load
            uiDevice.wait(Until.hasObject(By.pkg("com.ringapp").depth(0)), 1000);

            // Find and interact with UI elements to snooze.

            // Step 2: Wait for the snooze button to appear and then click the snooze button
            // We need to wait for it to appear because sometimes it takes a moment to load
            // App resource-id = com.ringapp:id/action_snooze
            UiObject2 snoozeButton = uiDevice.wait(Until.findObject(By.res("com.ringapp", "action_snooze")), 1000);

            // Check if the snooze button was found
            if (snoozeButton != null) {

                // Click snooze button to open snooze duration menu
                snoozeButton.click();

                // Step 3: Upon opening the separate menu for snooze duration, click to open drop down menu
                // App resource-id = com.ringapp:id/snoozeDurationCell
                UiObject2 snoozeDurationMenu = uiDevice.wait(Until.findObject(By.res("com.ringapp", "snoozeDurationCell")), 1000);
                snoozeDurationMenu.click();

                // Step 4: Select the appropriate snooze duration from the menu
                UiObject2 snoozeDurationOption = null;
                switch (snoozeDuration) {
                    case 30:
                        snoozeDurationOption = uiDevice.findObject(By.text("30 minutes"));
                        break;
                    case 1:
                        snoozeDurationOption = uiDevice.findObject(By.text("1 hour"));
                        break;
                    case 2:
                        snoozeDurationOption = uiDevice.findObject(By.text("2 hours"));
                        break;
                    case 3:
                        snoozeDurationOption = uiDevice.findObject(By.text("3 hours"));
                        break;
                    case 4:
                        snoozeDurationOption = uiDevice.findObject(By.text("4 hours"));
                        break;
                    case 8:
                        snoozeDurationOption = uiDevice.findObject(By.text("8 hours"));
                        break;
                    case 12:
                        snoozeDurationOption = uiDevice.findObject(By.text("12 hours"));
                        break;
                }

                if (snoozeDurationOption != null) {
                    snoozeDurationOption.click();
                } else {
                    // Handle the case where no matching option is found
                    Log.e("RingSnooze", "Snooze duration option not found");
                }

                // Step 5: Confirm snooze with clicking "Start Snooze"
                // App resource-id = com.ringapp:id/topButton
                UiObject2 startSnoozeButton = uiDevice.wait(Until.findObject(By.res("com.ringapp", "topButton")), 1000);
                if (startSnoozeButton != null) {
                    startSnoozeButton.click();
                } else {
                    // Handle the case where the button is not found
                    Log.e("RingSnooze", "Start Snooze button not found");
                }

            } else {
                // Handle the case where the snooze button is not found
                Log.e("RingSnooze", "Snooze button not found");
            }

        // Catch-all for any other error
        } catch (Exception e) {
            Log.e("RingSnooze", "Error triggering snooze automation", e);
        }
    }
}