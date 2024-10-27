package com.synclytic.ringsnoozewidget.app;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.List;

public class Accessibility extends AccessibilityService {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isRingAppInitialized = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Retrieve snooze duration from the intent
        int snoozeDuration = intent.getIntExtra("snooze_duration", 720); // 12 hours in minutes as default value

        Log.d("Accessibility", "(onStartCommand) Received command to snooze for: " + snoozeDuration + " minutes");

        return START_STICKY; // Keep the service running
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                && "com.ringapp".contentEquals(event.getPackageName())) {

            Log.d("onAccessibilityEvent", "(onAccessibilityEvent) Accessibility event received: " + event);

            // Only start if Ring app has not yet been initialized
            if (!isRingAppInitialized) {
                isRingAppInitialized = true;
                triggerRingSnoozeAutomation(this);
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.w("MyAccessibilityService", "(onInterrupt) Accessibility Service Interrupted");
    }

    private void triggerRingSnoozeAutomation(final Context context) {
        Log.d("Accessibility", "(triggerRingSnoozeAutomation) Initializing Ring app for snooze");

        // Step 1: Open the Ring app
        PackageManager packageManager = context.getPackageManager();
        Intent ringIntent = packageManager.getLaunchIntentForPackage("com.ringapp");

        if (ringIntent != null) {
            startActivity(ringIntent);
            Log.d("Accessibility", "(triggerRingSnoozeAutomation) Ring app initialized");

            handler.postDelayed(this::checkForSnoozeButton, 100); // Delay for Ring UI to develop
        } else {
            Log.e("Accessibility", "(triggerRingSnoozeAutomation) Ring app not found");
        }
    }

    private void checkForSnoozeButton() {
        // Check if the Ring app is open
        try {
            if ("com.ringapp".contentEquals(getRootInActiveWindow().getPackageName())) {
                Log.d("Accessibility", "(checkForSnoozeButton) Confirmed Ring app is open");

                // Find and click the "Snooze" button
                AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                if (rootNode != null) {
                    AccessibilityNodeInfo snoozeButton = findNodeById(rootNode, "com.ringapp:id/action_snooze");
                    if (snoozeButton != null) {
                        snoozeButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        Log.d("Accessibility", "(checkForSnoozeButton) Snooze button clicked");

                        // Wait for the next step
                        handler.postDelayed(this::openSnoozeDurationMenu, 250); // Delay for Ring UI to develop
                    } else {
                        Log.e("Accessibility", "(checkForSnoozeButton) Snooze button not found");
                    }
                } else {
                    Log.e("Accessibility", "(checkForSnoozeButton) Root node is null");
                }
            } else {
                Log.e("Accessibility", "(checkForSnoozeButton) Ring app not open");
            }
        } catch (Exception e) {
            Log.e("Accessibility", "(checkForSnoozeButton) Error checking snooze button", e);
        }
    }

    private void openSnoozeDurationMenu() {
        Log.d("Accessibility", "(openSnoozeDurationMenu) Opening snooze duration menu");

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            AccessibilityNodeInfo snoozeDurationMenu = findNodeById(rootNode, "com.ringapp:id/snoozeDurationCell");
            if (snoozeDurationMenu != null) {
                snoozeDurationMenu.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Log.d("Accessibility", "(openSnoozeDurationMenu) Snooze duration menu opened");

                // Wait for 2 seconds before selecting the 12-hour option
                handler.postDelayed(this::selectTwelveHourOption, 250); // Delay for Ring UI to develop
            } else {
                Log.e("Accessibility", "(openSnoozeDurationMenu) Snooze duration menu not found");
            }
        } else {
            Log.e("Accessibility", "(openSnoozeDurationMenu) Root node is null");
        }
    }

    private void selectTwelveHourOption() {
        Log.d("Accessibility", "(selectTwelveHourOption) Selecting 12-hour option");

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            AccessibilityNodeInfo twelveHourOption = findNodeById(rootNode, "com.ringapp:id/snooze_duration_12_hours");
            if (twelveHourOption != null) {
                twelveHourOption.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Log.d("Accessibility", "(selectTwelveHourOption) 12-hour snooze selected");

                // Wait for 2 seconds before confirming snooze
                handler.postDelayed(this::confirmSnooze, 250); // Delay for Ring UI to develop
            } else {
                Log.e("Accessibility", "(selectTwelveHourOption) 12-hour snooze option not found");
            }
        } else {
            Log.e("Accessibility", "(selectTwelveHourOption) Root node is null");
        }
    }

    private void confirmSnooze() {
        Log.d("Accessibility", "(confirmSnooze) Confirming snooze");

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            AccessibilityNodeInfo startSnoozeButton = findNodeById(rootNode, "com.ringapp:id/topButton");
            if (startSnoozeButton != null) {
                startSnoozeButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Log.d("Accessibility", "(confirmSnooze) Start Snooze clicked");

                // Reset initialization flag for future use
                isRingAppInitialized = false;
            } else {
                Log.e("Accessibility", "(confirmSnooze) Start Snooze button not found");
            }
        } else {
            Log.e("Accessibility", "(confirmSnooze) Root node is null");
        }
    }

    // Helper method to find UI element by ID
    private AccessibilityNodeInfo findNodeById(AccessibilityNodeInfo rootNode, String id) {
        List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByViewId(id);
        return (nodes != null && !nodes.isEmpty()) ? nodes.get(0) : null;
    }
}
