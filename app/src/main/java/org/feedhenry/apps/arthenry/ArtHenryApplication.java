package org.feedhenry.apps.arthenry;

import android.app.Application;

import org.feedhenry.apps.arthenry.injection.ApplicationModule;

import dagger.ObjectGraph;

public class ArtHenryApplication extends Application {
    private ObjectGraph objectGraph;

    @Override public void onCreate() {
        super.onCreate();
        objectGraph = ObjectGraph.create(new ApplicationModule(this));
        objectGraph.inject(this);
    }

    public ObjectGraph getObjectGraph() {
        return objectGraph;
    }

}
