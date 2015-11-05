package org.feedhenry.apps.arthenry.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import org.feedhenry.apps.arthenry.R;
import org.feedhenry.apps.arthenry.fh.FHClient;
import org.feedhenry.apps.arthenry.vo.MemeRequest;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.PipeManager;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by summers on 11/5/15.
 */
public class UploadService extends Service {

    private static final String TAG = UploadService.class.getSimpleName();

    public static final String FILE_URI = "UploadService.FILE_URI";
    private static final String TOP_MESSAGE = "UploadService.TOP_MESSAGE";
    private static final String BOTTOM_MESSAGE = "UploadService.BOTTOM_MESSAGE";

    private FHClient fhClient;

    private static final AtomicInteger notificationCount = new AtomicInteger(1);

    private final Handler handler;

    public UploadService() {
        HandlerThread thread = new HandlerThread(TAG);
        thread.start();
        handler = new Handler(thread.getLooper());
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        handler.post(new Runnable() {
            @Override
            public void run() {
                int id = 0;
                try {

                    Bundle extras = intent.getExtras();

                    String fileName = extras.getString(FILE_URI);
                    String ownerId = fhClient.getAccount().getId();
                    String topMessage = extras.getString(TOP_MESSAGE);
                    String bottomMessage = extras.getString(BOTTOM_MESSAGE);

                    if (fileName == null) {
                        displayErrorNotification("No file provided", 0);
                        return;
                    }

                    File file = new File(fileName);
                    id = displayUploadNotification(fileName);

                    PipeManager.getPipe("fh-upload").save(new MemeRequest.Builder().setBottomMessage(bottomMessage).setImage(file).setTopMessage(topMessage).setOwnerId(ownerId).build(), new UploadCallback(id));

                } catch (Exception e) {
                    displayErrorNotification(e.getMessage(), id);
                }

            }
        });

        return START_NOT_STICKY;
    }

    private int displayUploadNotification(String fileName) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle("Uploading...");
        builder.setContentText("Uploading " + fileName);
        builder.setProgress(0, 0, true);
        builder.setOngoing(true);

        Notification notification = builder.build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int id = notificationCount.getAndIncrement();

        notificationManager.notify(id, notification);

        return id;

    }

    private void displayErrorNotification(String message, int id) {

        if (id > 0) {
            clearUploadNotification(id);
        }

        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle("Upload Error");
        builder.setContentText(message);

        builder.setOngoing(false);

        Notification notification = builder.build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationCount.getAndIncrement(), notification);

    }

    private void clearUploadNotification(int id) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class UploadCallback implements Callback<MemeRequest> {

        private final int id;

        private UploadCallback(int id) {
            this.id = id;
        }


        @Override
        public void onSuccess(MemeRequest photoHolder) {
            clearUploadNotification(id);
        }

        @Override
        public void onFailure(Exception e) {
            Log.e(TAG, e.getMessage(), e);
            displayErrorNotification(e.getMessage(), id);
        }
    }


}