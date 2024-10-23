package com.synclytic.ringsnoozewidget.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import com.synclytic.ringsnoozewidget.R;

public class Widget_1hour extends MainApplication {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int snoozeTime = 1; // 1 hour

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ring_snooze_widget);

            Intent intent = new Intent(context, MainApplication.class);
            intent.setAction(RAGE_SNOOZE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.putExtra("snooze_duration", snoozeTime); // Pass snooze time
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            views.setOnClickPendingIntent(R.id.widgetIcon, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}