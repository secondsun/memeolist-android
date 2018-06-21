package org.aerogear.apps.memeolist;

import android.app.Application;

import org.aerogear.apps.memeolist.injection.ApplicationModule;

import dagger.ObjectGraph;

public class MemeolistApplication extends Application {
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
