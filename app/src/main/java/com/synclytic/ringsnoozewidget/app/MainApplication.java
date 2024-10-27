package com.synclytic.ringsnoozewidget.app;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MainApplication extends AppWidgetProvider {

    public static final String RAGE_SNOOZE = "com.synclytic.ringsnoozewidget.RAGE_SNOOZE";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d("MainApplication", "(onReceive) onReceive called with action: " + intent.getAction());

        if (RAGE_SNOOZE.equals(intent.getAction())) {

            Log.d("MainApplication", "(onReceive) RAGE_SNOOZE action triggered for snooze duration: " + intent.getIntExtra("snooze_duration", -1));

            // Get snooze duration from the intent
            int snoozeDuration = intent.getIntExtra("snooze_duration", 720); // 12 hours in minutes as default
            Log.d("MainApplication", "(onReceive) Snooze Duration from intent: " + snoozeDuration);

            // Start the Accessibility Service
            Intent accessibilityIntent = new Intent(context, Accessibility.class);
            accessibilityIntent.putExtra("snooze_duration", snoozeDuration);
            context.startService(accessibilityIntent);
        }
    }
}