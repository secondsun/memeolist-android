package org.feedhenry.apps.arthenry.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.feedhenry.apps.arthenry.ArtHenryApplication;
import org.feedhenry.apps.arthenry.R;
import org.feedhenry.apps.arthenry.ag.FHAuthModule;
import org.feedhenry.apps.arthenry.events.InitSuccessful;
import org.feedhenry.apps.arthenry.fh.FHClient;
import org.feedhenry.apps.arthenry.util.GsonUtil;
import org.feedhenry.apps.arthenry.vo.Comment;
import org.feedhenry.apps.arthenry.vo.Commit;
import org.feedhenry.apps.arthenry.vo.MemeRequest;
import org.feedhenry.apps.arthenry.vo.Project;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.PipeManager;
import org.jboss.aerogear.android.pipe.rest.RestfulPipeConfiguration;
import org.jboss.aerogear.android.pipe.rest.multipart.MultipartRequestBuilder;
import org.json.fh.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

/**
 * Created by summers on 11/5/15.
 */
public class UploadService extends Service {

    private static final String TAG = UploadService.class.getSimpleName();

    public static final String FILE_URI = "UploadService.FILE_URI";
    public static final String TOP_MESSAGE = "UploadService.TOP_MESSAGE";
    public static final String BOTTOM_MESSAGE = "UploadService.BOTTOM_MESSAGE";

    @Inject
    FHClient fhClient;
    @Inject
    Bus bus;

    private static final AtomicInteger notificationCount = new AtomicInteger(1);

    private final Handler handler;

    public UploadService() {
        HandlerThread thread = new HandlerThread(TAG);
        thread.start();
        handler = new Handler(thread.getLooper());

    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((ArtHenryApplication) getApplicationContext()).getObjectGraph().inject(this);
        bus.register(this);
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

                    Uri fileName = extras.getParcelable(FILE_URI);
                    String ownerId = fhClient.getAccount().getId();
                    String topMessage = extras.getString(TOP_MESSAGE, "").toUpperCase();
                    String bottomMessage = extras.getString(BOTTOM_MESSAGE, "").toUpperCase();

                    if (fileName == null) {
                        displayErrorNotification("No file provided", 0);
                        return;
                    }

                    InputStream file;
                    try {
                        file = getContentResolver().openInputStream(fileName);
                    } catch (FileNotFoundException e) {
                        file = new URL(fileName.toString()).openStream();
                    }

                    ByteArrayOutputStream out = new ByteArrayOutputStream();

                    Bitmap bitmap = getProportionalBitmap(BitmapFactory.decodeStream(file), 640);

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                    id = displayUploadNotification(fileName.toString());

                    InputStream in = new ByteArrayInputStream(out.toByteArray());

                    PipeManager.getPipe("fh-upload").save(
                            new MemeRequest.Builder().setBottomMessage(bottomMessage).setImage(in).setTopMessage(topMessage).setOwnerId(ownerId).build(), new UploadCallback(id)
                    );

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
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


    @Subscribe
    public void createPipe(InitSuccessful initSuccessful) {
        if (PipeManager.getPipe("fh-upload") == null) {
            synchronized (initSuccessful) {
                PipeManager.config("fh-upload", RestfulPipeConfiguration.class).module(new FHAuthModule(fhClient))
                        .withUrl(fhClient.getCloudUrl("/meme/create"))
                        .requestBuilder(new MultipartRequestBuilder())
                        .forClass(MemeRequest.class);
            }
        }
    }

    private void displayErrorNotification(String message, int id) {

        if (id > 0) {
            clearUploadNotification(id, null);
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

    private void clearUploadNotification(final int id, final String fileNameResult) {

        if (fileNameResult != null) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {

                        String accountId = fhClient.getAccount().getDisplayName();
                        Log.d(TAG, GsonUtil.GSON.toJson(fhClient.getAccount()));
                        TimeZone tz = TimeZone.getTimeZone("GMT");
                        Calendar calendar = Calendar.getInstance(tz);
                        Comment firstComment = new Comment();

                        DateFormat df = DateFormat.getDateTimeInstance();
                        df.setTimeZone(tz);

                        firstComment.setOwnerId(accountId);
                        firstComment.setComment("Posted " + df.format(calendar.getTime()));

                        Commit firstCommit = new Commit();
                        firstCommit.setOwnerId(accountId);
                        firstCommit.getComments().add(firstComment);
                        try {
                            firstCommit.setPhotoUrl(new URL(fileNameResult));
                        } catch (MalformedURLException ignore) {
                        }

                        Project project = new Project();

                        project.setOwnerId(accountId);
                        project.getCommits().add(firstCommit);

                        JSONObject projectJson = new JSONObject(GsonUtil.GSON.toJson(project));
                        try {
                            fhClient.getSyncClient().create("photos", projectJson);
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage(), e);

                        }

                    } catch (Exception ignore) {
                        Log.e(TAG, ignore.getMessage(), ignore);
                    } finally {
                        fhClient.getSyncClient().forceSync("photos");
                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(id);
                    }
                }
            });
        } else {
            fhClient.getSyncClient().forceSync("photos");
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(id);
        }


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
            clearUploadNotification(id, photoHolder.getFileNameResult());
        }

        @Override
        public void onFailure(Exception e) {
            Log.e(TAG, e.getMessage(), e);
            displayErrorNotification(e.getMessage(), id);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    public Bitmap getProportionalBitmap(Bitmap bitmap,
                                        int maxDimension) {
        if (bitmap == null) {
            return null;
        }
        Bitmap orig = bitmap;

        float xyRatio = 0;
        int newWidth = 0;
        int newHeight = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            xyRatio = (float) maxDimension / bitmap.getWidth();
            newHeight = (int) (bitmap.getHeight() * xyRatio);
            bitmap = Bitmap.createScaledBitmap(
                    bitmap, maxDimension, newHeight, true);
        } else {
            xyRatio = (float) maxDimension / bitmap.getHeight();
            newWidth = (int) (bitmap.getWidth() * xyRatio);
            bitmap = Bitmap.createScaledBitmap(
                    bitmap, newWidth, maxDimension, true);
        }
        if (orig != bitmap) {
            orig.recycle();
        }
        return bitmap;
    }
}