package com.synclytic.ringsnoozewidget.app;

import com.synclytic.ringsnoozewidget.R;
import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.core.app.NotificationCompat;
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

        // Try opening the Ring app
        PackageManager packageManager = getPackageManager();
        Intent ringIntent = packageManager.getLaunchIntentForPackage("com.ringapp");

        if (ringIntent != null) {
            ringIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(ringIntent);
            Log.d("Accessibility", "(onStartCommand) Successfully launching Ring app.");
        } else {
            Log.d("Accessibility", "(onStartCommand) No direct access to Ring UI. Attempting through notifications.");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = "ring_snooze_widget_channel";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Ring Snooze Widget",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationManager.createNotificationChannel(channel);
            }

            Intent openIntent = new Intent(Intent.ACTION_MAIN);
            openIntent.setPackage("com.ringapp");
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    openIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            Notification notification = new NotificationCompat.Builder(this, channelId)
                    .setContentTitle("Open Ring App")
                    .setContentText("Tap to open the Ring app and activate snooze.")
                    .setSmallIcon(R.drawable.widget_logo_12)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setFullScreenIntent(pendingIntent, true)
                    .build();

            notificationManager.notify(1, notification);

        }

        // Return START_NOT_STICKY as no further handling required after app launch
        return START_NOT_STICKY;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                && "com.ringapp".contentEquals(event.getPackageName())
                && !isSnoozeActive) {

            Log.d("Accessibility", "(onAccessibilityEvent) Accessibility event received: " + event);
            Log.d("Accessibility", "(onAccessibilityEvent) Event class name: " + event.getClassName());

            // Only start if Ring app has not yet been initialized
            if (!isRingAppInitialized) {
                isRingAppInitialized = true;
                Log.d("Accessibility", "(onAccessibilityEvent) Initializing Ring app snooze automation...");

                handler.postDelayed(this::checkForSnoozeButton, 1000); // Delay to let Ring UI fully load
                // 500 millis worked when app was already open, but fails when app is closed
                // This is usually due to the latent load of the snooze button, in place of the invite friends button
                // 1000 millis works fine for now on a cold start
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

            // Find the 12-hour option directly from the root
            List<AccessibilityNodeInfo> allNodes = rootNode.findAccessibilityNodeInfosByText("12 hours");
            for (AccessibilityNodeInfo node : allNodes) {
                if (node.getClassName().equals("com.ring.android.safe.cell.IconValueCell")) {
                    // Click the 12-hour option if found
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.d("Accessibility", "(selectTwelveHourOption) 12-hour snooze selected");

                    handler.postDelayed(this::confirmSnooze, 250); // Delay for Ring UI to develop
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
    // We are the devs who say ni!
    private void logNodeHierarchy(AccessibilityNodeInfo node, String indent) {
        Log.d("Accessibility", indent + "Node: " + node.getClassName() + ", Text: " + node.getText());
        for (int ni = 0; ni < node.getChildCount(); ni++) {
            logNodeHierarchy(node.getChild(ni), indent + "  ");
        }
    }
}

