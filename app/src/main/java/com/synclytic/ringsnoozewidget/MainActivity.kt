package com.synclytic.ringsnoozewidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import com.synclytic.ringsnoozewidget.app.MainApplication

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Trigger widget update
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val myWidget = ComponentName(this, MainApplication::class.java)
        appWidgetManager.updateAppWidget(myWidget, RemoteViews(packageName, R.layout.ring_snooze_widget))

        // Finish the activity (since we don't need the activity to stay open)
        finish()

    }
}