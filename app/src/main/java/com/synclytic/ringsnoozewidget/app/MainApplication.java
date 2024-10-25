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
import java.util.Arrays;


public class MainApplication extends AppWidgetProvider {

    public static final String RAGE_SNOOZE = "com.synclytic.ringsnoozewidget.RAGE_SNOOZE";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Log.d("MainApplication", "(onUpdate) onUpdate called with appWidgetIds: " + Arrays.toString(appWidgetIds));

        for (int appWidgetId : appWidgetIds) {

            // Get snooze duration from preferences
            SharedPreferences prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE);
            int snoozeDuration = prefs.getInt("snooze_duration_" + appWidgetId, -1);
            Log.d("MainApplication", "(onUpdate) Snooze Duration for widget " + appWidgetId + ": " + snoozeDuration);

            // Get the icon resource ID based on the snooze duration
            int iconId = getIconIdForSnoozeDuration(snoozeDuration);

            // Create RemoteViews for the widget
            Log.d("MainApplication", "(onUpdate) Creating RemoteViews for widgetId: " + appWidgetId);
            RemoteViews widgetViews = new RemoteViews(context.getPackageName(), R.layout.ring_snooze_widget);

            // Set the icon on the ImageView
            Log.d("MainApplication", "(onUpdate) Setting icon with ID: " + iconId);
            widgetViews.setImageViewResource(R.id.widgetIcon, iconId);

            // Intent to trigger the snooze action
            Intent snoozeIntent = new Intent(context, MainApplication.class);
            snoozeIntent.setAction(RAGE_SNOOZE);
            snoozeIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            snoozeIntent.putExtra("snooze_duration", snoozeDuration);
            Log.d("MainApplication", "(onUpdate) Creating PendingIntent for widgetId: " + appWidgetId);
            PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, appWidgetId, snoozeIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            if (snoozeDuration != -1) {

                // Set click listener on the widget's ImageView
                widgetViews.setOnClickPendingIntent(R.id.widgetIcon, snoozePendingIntent);

                appWidgetManager.updateAppWidget(appWidgetId, widgetViews);
            }
        }
    }

    // Helper function to get icon ID based on snooze duration
    private int getIconIdForSnoozeDuration(int snoozeDuration) {
        return switch (snoozeDuration) {
            case 30 -> R.drawable.widget_logo_30m;
            case 1 -> R.drawable.widget_logo_1;
            case 2 -> R.drawable.widget_logo_2;
            case 3 -> R.drawable.widget_logo_3;
            case 4 -> R.drawable.widget_logo_4;
            case 8 -> R.drawable.widget_logo_8;
            case 12 -> R.drawable.widget_logo_12;
            default -> R.drawable.widget_logo_12; // Default icon
        };
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d("MainApplication", "(onReceive) onReceive called with action: " + intent.getAction());

        if (RAGE_SNOOZE.equals(intent.getAction())) {

            Log.d("MainApplication", "(onReceive) RAGE_SNOOZE action received");

            // Get snooze duration from the intent
            int snoozeDuration = intent.getIntExtra("snooze_duration", -1);
            Log.d("MainApplication", "(onReceive) Snooze Duration from intent: " + snoozeDuration);

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

            Log.d("MainApplication", "(triggerRingSnoozeAutomation) Starting Ring app for snooze");

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

                Log.d("MainApplication", "(triggerRingSnoozeAutomation) Snooze button found, clicking it");
                // Click snooze button to open snooze duration menu
                snoozeButton.click();

                // Step 3: Upon opening the separate menu for snooze duration, click to open drop down menu
                // App resource-id = com.ringapp:id/snoozeDurationCell
                UiObject2 snoozeDurationMenu = uiDevice.wait(Until.findObject(By.res("com.ringapp", "snoozeDurationCell")), 1000);
                snoozeDurationMenu.click();

                // Step 4: Select the appropriate snooze duration from the menu
                UiObject2 snoozeDurationOption = switch (snoozeDuration) {
                    case 30 -> uiDevice.findObject(By.text("30 minutes"));
                    case 1 -> uiDevice.findObject(By.text("1 hour"));
                    case 2 -> uiDevice.findObject(By.text("2 hours"));
                    case 3 -> uiDevice.findObject(By.text("3 hours"));
                    case 4 -> uiDevice.findObject(By.text("4 hours"));
                    case 8 -> uiDevice.findObject(By.text("8 hours"));
                    case 12 -> uiDevice.findObject(By.text("12 hours"));
                    default -> null;
                };

                if (snoozeDurationOption != null) {
                    snoozeDurationOption.click();
                } else {
                    // Handle the case where no matching option is found
                    Log.e("RingSnooze", "(triggerRingSnoozeAutomation) Snooze duration option not found");
                }

                // Step 5: Confirm snooze with clicking "Start Snooze"
                // App resource-id = com.ringapp:id/topButton
                UiObject2 startSnoozeButton = uiDevice.wait(Until.findObject(By.res("com.ringapp", "topButton")), 1000);
                if (startSnoozeButton != null) {
                    startSnoozeButton.click();
                } else {
                    // Handle the case where the button is not found
                    Log.e("RingSnooze", "(triggerRingSnoozeAutomation) Start Snooze button not found");
                }

            } else {
                // Handle the case where the snooze button is not found
                Log.e("RingSnooze", "(triggerRingSnoozeAutomation) Snooze button not found");
            }

        // Catch-all for any other error
        } catch (Exception e) {
            Log.e("RingSnooze", "(triggerRingSnoozeAutomation) Error triggering snooze automation", e);
        }
    }
}