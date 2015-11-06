package org.feedhenry.apps.arthenry.injection;

import android.content.Context;

import com.feedhenry.sdk.sync.FHSyncListener;
import com.feedhenry.sdk.sync.NotificationMessage;
import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.feedhenry.apps.arthenry.ArtHenryApplication;
import org.feedhenry.apps.arthenry.MainActivity;
import org.feedhenry.apps.arthenry.SplashScreen;
import org.feedhenry.apps.arthenry.events.ProjectsAvailable;
import org.feedhenry.apps.arthenry.fh.FHClient;
import org.feedhenry.apps.arthenry.fh.auth.FHAuthClientConfig;
import org.feedhenry.apps.arthenry.fh.sync.AbstractSyncListener;
import org.feedhenry.apps.arthenry.fh.sync.FHSyncClientConfig;
import org.feedhenry.apps.arthenry.service.UploadService;
import org.feedhenry.apps.arthenry.util.adapter.PicassoDownloader;
import org.feedhenry.apps.arthenry.view.CreateProjectDialog;
import org.feedhenry.apps.arthenry.view.MainFragment;
import org.feedhenry.apps.arthenry.view.ProjectDetailDialog;
import org.feedhenry.apps.arthenry.vo.Project;
import org.json.fh.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                ArtHenryApplication.class, MainActivity.class, SplashScreen.class, UploadService.class, CreateProjectDialog.class, ProjectDetailDialog.class, MainFragment.class
        }
)
public class ApplicationModule {

    private final Context context;

    private final Bus bus = new Bus();
    private PicassoDownloader downloader;
    public ApplicationModule(Context context) {
        this.context = context.getApplicationContext();
    }
    private final static List<Project> projects = new ArrayList<>();
    @Provides
    @Singleton
    public FHClient provideFHClient() {

        final AtomicReference<FHClient> clientRef = new AtomicReference<>();

        final FHSyncListener syncListener =  new AbstractSyncListener() {
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
                        Project item = new Gson().fromJson(dataObj.toString(), Project.class);
                        item.setId(key);
                        itemsToSync.add(item);
                    }

                    projects.clear();
                    projects.addAll(itemsToSync);
                    bus.post(new ProjectsAvailable(projects));

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
                        Project item = new Gson().fromJson(dataObj.toString(), Project.class);
                        item.setId(key);
                        itemsToSync.add(item);
                    }

                    projects.clear();
                    projects.addAll(itemsToSync);
                    bus.post(new ProjectsAvailable(projects));

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
        clientRef.set(fhclient);;

        return fhclient;
    }

    @Produce
    public ProjectsAvailable getProjectsAvailable() {
        return new ProjectsAvailable(projects);
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
    };

}
