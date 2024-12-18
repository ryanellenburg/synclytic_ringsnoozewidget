package com.synclytic.ringsnoozewidget.app;

import static com.synclytic.ringsnoozewidget.app.MainApplication.RAGE_SNOOZE;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import com.synclytic.ringsnoozewidget.R;
import java.util.Arrays;

public class Widget_12hour extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("Widget_12hour", "(onUpdate) onUpdate called for widget IDs: " + Arrays.toString(appWidgetIds));

        final int snoozeTime = 720; // 12 hours in minutes

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ring_snooze_widget);

            // Set the image resource for the widget icon
            views.setImageViewResource(R.id.widgetIcon, R.drawable.widget_logo_12);

            Log.d("Widget_12hour", "(onUpdate) Creating PendingIntent for widgetId: " + appWidgetId);
            Intent intent = new Intent(context, MainApplication.class);
            intent.setAction(RAGE_SNOOZE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.putExtra("snooze_duration", snoozeTime); // Pass snooze time
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            views.setOnClickPendingIntent(R.id.widgetIcon, pendingIntent);
            Log.d("Widget_12hour", "(onUpdate) Set click listener on widgetIcon for widgetId: " + appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}