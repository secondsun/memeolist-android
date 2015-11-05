package org.feedhenry.apps.arthenry.injection;

import android.content.Context;

import com.squareup.otto.Bus;

import org.feedhenry.apps.arthenry.ArtHenryApplication;
import org.feedhenry.apps.arthenry.MainActivity;
import org.feedhenry.apps.arthenry.SplashScreen;
import org.feedhenry.apps.arthenry.fh.FHClient;
import org.feedhenry.apps.arthenry.fh.auth.FHAuthClientConfig;
import org.feedhenry.apps.arthenry.fh.sync.FHSyncClientConfig;
import org.feedhenry.apps.arthenry.fh.sync.InjectableSyncListener;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                ArtHenryApplication.class, MainActivity.class, SplashScreen.class
        }
)
public class ApplicationModule {

    private final Context context;
    private final InjectableSyncListener syncListener =  new InjectableSyncListener();
    private final Bus bus = new Bus();
    public ApplicationModule(Context context) {
        this.context = context.getApplicationContext();
    }

    @Provides
    @Singleton
    public FHClient provideFHClient() {
        return new FHClient.Builder(context, bus)
                .addFeature(new FHSyncClientConfig(syncListener)
                        .addDataSet("photos")
                        .setNotifySyncStarted(true)
                        .setAutoSyncLocalUpdates(true)
                        .setNotifySyncComplete(true)
                        .setSyncFrequencySeconds(10))
                .addFeature(new FHAuthClientConfig("Google"))
                .build();
    }

    @Provides
    @Singleton
    public InjectableSyncListener getSyncListener() {
        return this.syncListener;
    }

    @Provides
    @Singleton
    public Bus getBus() {
        return this.bus;
    }

}
