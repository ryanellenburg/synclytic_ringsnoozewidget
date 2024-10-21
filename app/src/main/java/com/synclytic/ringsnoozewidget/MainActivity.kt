package com.synclytic.ringsnoozewidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.synclytic.ringsnoozewidget.ring.RingSnoozeWidget

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // TODO continue any initialization & setup code

        val appWidgetManager = AppWidgetManager.getInstance(this)
        val myWidget = ComponentName(this, RingSnoozeWidget::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(myWidget)
        if (appWidgetIds.isNotEmpty()) {
            RingSnoozeWidget().onUpdate(this, appWidgetManager, appWidgetIds)
        }

        // Finish the activity (since we don't need the activity to stay open)
        finish()

    }
}