package com.synclytic.ringsnoozewidget.app;

import android.accessibilityservice.AccessibilityService;
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
    private boolean isSnoozeActive = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Reset snooze status at service start
        isSnoozeActive = false;
        Log.d("Accessibility", "(onStartCommand) Resetting snooze status.");

        // Retrieve snooze duration from the intent
        int snoozeDuration = intent.getIntExtra("snooze_duration", 720); // 12 hours in minutes
        Log.d("Accessibility", "(onStartCommand) Received command to snooze for: " + snoozeDuration + " minutes");

        // Step 1: Open the Ring app
        PackageManager packageManager = getPackageManager();
        Intent ringIntent = packageManager.getLaunchIntentForPackage("com.ringapp");

        if (ringIntent != null) {
            ringIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(ringIntent);
            Log.d("Accessibility", "(onStartCommand) Successfully launching Ring app with getLaunchIntentForPackage");
        } else {
            // Alternative approach: try to launch a known activity directly
            Log.e("Accessibility", "(onStartCommand) Failed with getLaunchIntentForPackage, trying alternative method");

            ringIntent = new Intent();
            ringIntent.setClassName("com.ringapp", "com.ringapp.maindashboard.MyDevicesDashboardActivity");
            ringIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            try {
                startActivity(ringIntent);
                Log.d("Accessibility", "(onStartCommand) Successfully launched Ring app using direct activity Intent");
            } catch (Exception e) {
                Log.e("Accessibility", "(onStartCommand) Failed to launch Ring app with alternative method", e);
            }
        }

        return START_NOT_STICKY; // Prevent automatic restarting of the service
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        // Check if snooze is already active
        if (isSnoozeActive) {
            handler.postDelayed(this::stopSelf, 1000); // Delay for 1 second
            Log.d("Accessibility", "(onAccessibilityEvent) Snooze already active, skipping further actions.");
            return;
        }

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                && "com.ringapp".contentEquals(event.getPackageName())) {

            Log.d("Accessibility", "(onAccessibilityEvent) Accessibility event received: " + event);
            Log.d("Accessibility", "(onAccessibilityEvent) Event class name: " + event.getClassName());

            // Only start if Ring app has not yet been initialized
            if (!isRingAppInitialized) {
                isRingAppInitialized = true;
                Log.d("Accessibility", "(onAccessibilityEvent) Initializing Ring app snooze automation...");

                handler.postDelayed(this::checkForSnoozeButton, 250); // Delay to let Ring UI fully load
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.w("Accessibility", "(onInterrupt) Accessibility Service Interrupted");
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

                        handler.postDelayed(this::openSnoozeDurationMenu, 500); // Delay for Ring UI to develop
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

                handler.postDelayed(this::selectTwelveHourOption, 500); // Delay for Ring UI to develop
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
            // Log the entire hierarchy starting from the root node
            logNodeHierarchy(rootNode, "");

            // Attempt to find the 12-hour option directly from the root
            List<AccessibilityNodeInfo> allNodes = rootNode.findAccessibilityNodeInfosByText("12 hours");
            for (AccessibilityNodeInfo node : allNodes) {
                if (node.getClassName().equals("com.ring.android.safe.cell.IconValueCell")) {
                    // Click the 12-hour option if found
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.d("Accessibility", "(selectTwelveHourOption) 12-hour snooze selected");

                    handler.postDelayed(this::confirmSnooze, 250);
                    return;
                }
            }
            Log.e("Accessibility", "(selectTwelveHourOption) 12-hour snooze option not found");
        } else {
            Log.e("Accessibility", "(selectTwelveHourOption) Root node is null");
        }
    }

    private void confirmSnooze() {
        Log.d("Accessibility", "(confirmSnooze) Confirming snooze");

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            // Try to find primaryButton first, then topButton
            AccessibilityNodeInfo startSnoozeButton = findNodeById(rootNode, "com.ringapp:id/primaryButton");
            if (startSnoozeButton == null) {
                startSnoozeButton = findNodeById(rootNode, "com.ringapp:id/topButton");
            }

            if (startSnoozeButton != null) {
                startSnoozeButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Log.d("Accessibility", "(confirmSnooze) Start Snooze clicked");

                // Stop the Accessibility Service after snooze is confirmed
                stopSelf();

                // Go to the home screen
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeIntent);

                // Set the flag to true to avoid further snooze attempts
                isSnoozeActive = true;
                isRingAppInitialized = false;

                Log.d("Accessibility", "(confirmSnooze) Accessibility Service stopped after successful snooze");

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

    // Method to log the hierarchy of nodes
    private void logNodeHierarchy(AccessibilityNodeInfo node, String indent) {
        Log.d("Accessibility", indent + "Node: " + node.getClassName() + ", Text: " + node.getText());
        for (int ni = 0; ni < node.getChildCount(); ni++) {
            logNodeHierarchy(node.getChild(ni), indent + "  ");
        }
    }
}

