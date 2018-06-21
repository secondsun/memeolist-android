package org.aerogear.apps.memeolist.injection;

import android.content.Context;

import com.feedhenry.sdk.sync.FHSyncListener;
import com.feedhenry.sdk.sync.NotificationMessage;
import com.squareup.otto.Bus;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.aerogear.apps.memeolist.ImagePickerActivity;
import org.aerogear.apps.memeolist.MainActivity;
import org.aerogear.apps.memeolist.MemeolistApplication;
import org.aerogear.apps.memeolist.SplashScreen;
import org.aerogear.apps.memeolist.events.ProjectsAvailable;
import org.aerogear.apps.memeolist.fh.FHClient;
import org.aerogear.apps.memeolist.fh.auth.FHAuthClientConfig;
import org.aerogear.apps.memeolist.fh.sync.AbstractSyncListener;
import org.aerogear.apps.memeolist.fh.sync.FHSyncClientConfig;
import org.aerogear.apps.memeolist.service.UploadService;
import org.aerogear.apps.memeolist.util.GsonUtil;
import org.aerogear.apps.memeolist.util.adapter.PicassoDownloader;
import org.aerogear.apps.memeolist.view.CreateProjectDialog;
import org.aerogear.apps.memeolist.view.MainFragment;
import org.aerogear.apps.memeolist.view.ProjectDetailDialog;
import org.aerogear.apps.memeolist.vo.Project;
import org.json.fh.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                MemeolistApplication.class, MainActivity.class, SplashScreen.class, UploadService.class, CreateProjectDialog.class, ProjectDetailDialog.class, MainFragment.class, ImagePickerActivity.class
        }
)
public class ApplicationModule {

    private final Context context;

    private final Bus bus = new Bus();
    private PicassoDownloader downloader;

    public ApplicationModule(Context context) {
        this.context = context.getApplicationContext();
    }

    private final static Set<Project> projects = new TreeSet<>();

    @Provides
    @Singleton
    public FHClient provideFHClient() {

        final AtomicReference<FHClient> clientRef = new AtomicReference<>();

        final FHSyncListener syncListener = new AbstractSyncListener() {
            @Override
            public void onSyncCompleted(NotificationMessage notificationMessage) {
                FHClient fhClient = clientRef.get();
                if (fhClient != null) {
                    JSONObject allData = fhClient.getSyncClient().list("photos");


                    Iterator<String> it = allData.keys();
                    List<Project> itemsToSync = new ArrayList<>();

                    while (it.hasNext()) {
                        String key = it.next();
                        JSONObject data = allData.getJSONObject(key);
                        JSONObject dataObj = data.getJSONObject("data");
                        Project item = GsonUtil.GSON.fromJson(dataObj.toString(), Project.class);
                        item.setId(key);
                        itemsToSync.add(item);
                    }

                    projects.addAll(itemsToSync);
                    bus.post(new ProjectsAvailable(new ArrayList<Project>(projects)));

                }
            }

            @Override
            public void onLocalUpdateApplied(NotificationMessage notificationMessage) {
                FHClient fhClient = clientRef.get();
                if (fhClient != null) {
                    JSONObject allData = fhClient.getSyncClient().list("photos");


                    Iterator<String> it = allData.keys();
                    List<Project> itemsToSync = new ArrayList<>();

                    while (it.hasNext()) {
                        String key = it.next();
                        JSONObject data = allData.getJSONObject(key);
                        JSONObject dataObj = data.getJSONObject("data");
                        Project item = GsonUtil.GSON.fromJson(dataObj.toString(), Project.class);
                        item.setId(key);
                        itemsToSync.add(item);
                    }

                    projects.addAll(itemsToSync);
                    bus.post(new ProjectsAvailable(new ArrayList<Project>(projects)));

                }
            }
        };

        FHClient fhclient = new FHClient.Builder(context, bus)
                .addFeature(new FHSyncClientConfig(syncListener)
                        .addDataSet("photos")
                        .setNotifySyncStarted(true)
                        .setAutoSyncLocalUpdates(true)
                        .setNotifySyncComplete(true)
                        .setSyncFrequencySeconds(300))
                .addFeature(new FHAuthClientConfig("Google"))
                .build();
        clientRef.set(fhclient);
        ;

        return fhclient;
    }

    @Provides
    @Singleton
    public Bus getBus() {
        return this.bus;
    }

    @Provides
    @Singleton
    public Picasso getPicasso() {
        return new Picasso.Builder(context).downloader(new OkHttpDownloader(PicassoDownloader.picassoClient)).build();
    }

    ;

}
