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
import androidx.test.uiautomator.UiObject2;
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

        // Using UI Automator
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        try {

            // Step 1: Open the Ring app
            ComponentName ringApp = new ComponentName("com.ringapp", "com.ringapp.MainActivity"); // Replace with actual component name
            Intent ringIntent = new Intent();
            ringIntent.setComponent(ringApp);
            ringIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(ringIntent);

            // Step 2: Wait for the app to load TODO figure out and adjust the timeout
            uiDevice.wait(Until.hasObject(By.pkg("com.ringapp").depth(0)), 5000);

            // Find and interact with UI elements to snooze.

            // Step 3: Wait for the snooze button to appear TODO adjust timeout as needed
            UiObject2 snoozeButton = uiDevice.wait(Until.findObject(By.res("com.ringapp", "snooze_button_id")), 1000); // Wait 1 second (adjust to 2000 for 2 seconds if needed)

            // Check if the snooze button was found
            if (snoozeButton != null) {

                // Step 4: Click snooze button to open snooze duration menu
                snoozeButton.click();

                // Step 5: Upon opening the separate menu for snooze duration, click to open drop down menu
                UiObject2 snoozeDurationMenu = uiDevice.findObject(By.res("com.ringapp", "snooze_duration_menu_id"));
                snoozeDurationMenu.click();

                // Step 6: Select the appropriate snooze duration from the menu
                if (snoozeDuration == 30) {
                    UiObject2 snooze30mOption = uiDevice.findObject(By.text("30 minutes"));
                    snooze30mOption.click();
                } else if (snoozeDuration == 1) {
                    UiObject2 snooze1hOption = uiDevice.findObject(By.text("1 hour"));
                    snooze1hOption.click();
                } else if (snoozeDuration == 2) {
                    UiObject2 snooze2hOption = uiDevice.findObject(By.text("2 hours"));
                    snooze2hOption.click();
                } else if (snoozeDuration == 3) {
                    UiObject2 snooze3hOption = uiDevice.findObject(By.text("3 hours"));
                    snooze3hOption.click();
                } else if (snoozeDuration == 4) {
                    UiObject2 snooze4hOption = uiDevice.findObject(By.text("4 hours"));
                    snooze4hOption.click();
                } else if (snoozeDuration == 8) {
                    UiObject2 snooze8hOption = uiDevice.findObject(By.text("8 hours"));
                    snooze8hOption.click();
                } else if (snoozeDuration == 12) {
                    UiObject2 snooze12hOption = uiDevice.findObject(By.text("12 hours"));
                    snooze12hOption.click();
                }

                // Step 7: Confirm snooze with clicking "Start Snooze"
                UiObject2 startSnoozeButton = uiDevice.findObject(By.res("com.ringapp", "start_snooze_button_id")); // Replace with the actual resource ID
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